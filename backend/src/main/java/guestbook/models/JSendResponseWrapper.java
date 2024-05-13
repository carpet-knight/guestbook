package guestbook.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSendResponseWrapper {
    private final JSendStatus status;
    private final Object data;
    private final String message;

    public JSendResponseWrapper(JSendStatus status, Object data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public JSendResponseWrapper(JSendStatus status, Object data) {
        this(status, data, null);
    }

    public JSendResponseWrapper(JSendStatus status, String message) {
        this(status, null, message);
    }

    public String getStatus() {
        return status.toString().toLowerCase();
    }

    public Object getData() {
        Object data = this.data;
        if (data == null && this.message != null) {
            data = new LinkedHashMap<String, String>() {{
                put("message", getMessage());
            }};
        }
        return data;
    }

    public String getMessage() {
        return message;
    }

    Map<String, Object> asMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", getStatus());

        if (data != null)
            map.put("data", getData());

        if (message != null)
            map.put("message", getMessage());

        return map;
    }
}
