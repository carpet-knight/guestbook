package guestbook;

import guestbook.processor.PictureProcessListener;
import guestbook.processor.PictureProcessor;
import guestbook.processor.PictureProcessorNotifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Repository
@Profile("postgres")
public class PostgresGuestBookService implements GuestBookService, PictureProcessListener {
    private final JdbcTemplate jdbc;
    private final Logger logger;
    private final PictureProcessor processor;

    @Autowired
    public PostgresGuestBookService(JdbcTemplate jdbc, PictureProcessor processor) {
        this.jdbc = jdbc;
        logger = LoggerFactory.getLogger(PostgresGuestBookService.class);

        this.processor = processor;
        if (processor instanceof PictureProcessorNotifier)
            ((PictureProcessorNotifier) processor).registerListener(this);
    }

    class EntryRequestExtractor implements ResultSetExtractor<GuestBookEntryRequest> {

        @Override
        public GuestBookEntryRequest extractData(@NotNull ResultSet resultSet) throws SQLException, DataAccessException {
            List<GuestBookEntryRequest> entryList = new EntryRequestListExtractor().extractData(resultSet);
            assert (entryList != null && entryList.size() == 1);

            return entryList.get(0);
        }
    }

    class EntryRequestListExtractor implements ResultSetExtractor<List<GuestBookEntryRequest>> {

        @Override
        public List<GuestBookEntryRequest> extractData(ResultSet rs) throws SQLException, DataAccessException {
            // Precondition: the resultset is ordered by entryId!
            GuestBookEntryRequest current = null;
            GuestBookEntry currentEntry = null;
            List<GuestBookEntryRequest> requests = new LinkedList<>();

            while (rs.next()) {
                if (currentEntry != null && !rs.getString("entry_id").equals(currentEntry.getEntryId())) {
                    // New ID, new entry. Let's first add the previous one to the list
                    requests.add(current);
                    current = null;
                }

                if (current == null) {
                    // And then create a new one
                    current = extractEntryRequest(rs);
                    currentEntry = current.getEntry();
                }

                // Adding an additional size to the entry we're making
                String urlInDb = rs.getString("picture_url");
                URL pictureUrl = null;
                if (urlInDb != null) {
                    try {
                        pictureUrl = new URL(urlInDb);
                    } catch (MalformedURLException e) {
                        logger.warn("Malformatted URL in database", e);
                        continue;
                    }
                }

                assert (currentEntry != null);
                currentEntry.getResizedPictures().put(rs.getString("size"), pictureUrl);
            }

            // Add the last entry to the list
            if (current != null)
                requests.add(current);

            return requests;
        }

        private GuestBookEntryRequest extractEntryRequest(ResultSet rs) throws SQLException {
            OffsetDateTime createdAt = rs.getObject("created_at", OffsetDateTime.class);
            GuestBookEntry entry;
            try {
                entry = new GuestBookEntry(
                        rs.getString("entry_id"),
                        rs.getString("author"),
                        rs.getString("message"),
                        new URL(rs.getString("source_picture_url")),
                        createdAt
                );
            } catch (MalformedURLException e) {
                logger.error("Malformatted source URL in database");
                throw new SQLException(e);
            }

            GuestBookEntryRequest request = new GuestBookEntryRequest(entry);
            request.setState(EntryRequestState.valueOf(rs.getString("state").toUpperCase()));
            request.setStatusMessage(rs.getString("error_message"));
            return request;
        }
    }

    @Override
    public GuestBookEntryRequest getEntry(String id) {
        String query = "select * from " +
                "(select entry_id, size_name as size from entries cross join sizes where entry_id = ?) s " +
                "left join resized_pictures using (entry_id, size)\n" +
                "left join entries e on s.entry_id = e.entry_id;";
        return jdbc.query(query, new EntryRequestExtractor(), id);
    }

    @Override
    public Iterable<GuestBookEntry> getLastTenEntries() {
        List<GuestBookEntryRequest> list = jdbc.query("SELECT * FROM last_ten_entries;", new EntryRequestListExtractor());
        List<GuestBookEntry> result = new ArrayList<>(10);

        assert (list != null);

        for (GuestBookEntryRequest request : list)
            result.add(request.getEntry());

        return result;
    }

    @Override
    public void schedule(GuestBookEntryRequest request) {
        GuestBookEntry entry = request.getEntry();

        jdbc.update("INSERT INTO entries (entry_id, author, message, source_picture_url, created_at) " +
                        "VALUES (?, ?, ?, ?, ?);",
                entry.getEntryId(),
                entry.getAuthor(),
                entry.getMessage(),
                entry.getSourcePictureUrl().toString(),
                entry.getCreatedAt());

        processor.schedule(request);
    }

    @Override
    public void processingComplete(GuestBookEntryRequest request) {
        GuestBookEntry entry = request.getEntry();

        // Update metadata
        jdbc.update("UPDATE entries SET state=?::entry_state, error_message=? WHERE entry_id=?;",
                request.getState().toString(),
                request.getStatusMessage(),
                entry.getEntryId());
    }

    @Override
    public void pictureSizeReady(GuestBookEntryRequest request, String size) {
        GuestBookEntry entry = request.getEntry();
        URL hostedPicture = entry.getResizedPictures().get(size);

        jdbc.update("INSERT INTO resized_pictures (entry_id, size, picture_url) VALUES (?,?,?) " +
                        "ON CONFLICT DO NOTHING;",
                entry.getEntryId(),
                size,
                hostedPicture.toString());
    }
}
