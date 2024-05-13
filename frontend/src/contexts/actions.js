import {types} from './reducers'

export const useActions = (state, dispatch) => {
  function startEntryWatching({entry, entryId}) {
    dispatch({type: types.START_POLLING, payload: {entry, entryId}})
  }
  function stopEntriesWatching(entryIds) {
    dispatch({type: types.STOP_POLLING, payload: {entryIds}})
  }
  function receiveEntries(posts) {
    dispatch({type: types.RECEIVE_POSTS, payload: {posts}})
  }

  return {
    startEntryWatching,
    receiveEntries,
    stopEntriesWatching,
  }
}
