import {emptyArray} from '../utils/empty'

const initialState = Object.create(null)

initialState.watchedEntries = []
initialState.posts = Object.create(null)

const types = {
  RECEIVE_POSTS: 'RECEIVE_POSTS',
  START_POLLING: 'START_POLLING',
  STOP_POLLING: 'STOP_POLLING',
}

const watchedEntriesReducer = (state = initialState.watchedEntries, action) => {
  switch (action.type) {
    case types.START_POLLING:
      return [...state, action.payload.entryId]
    case types.STOP_POLLING:
      return state.filter(item => !action.payload.entryIds.includes(item))
    case types.RECEIVE_POSTS:
      const ids = {
        completed: [],
        processing: [],
      }

      Object.entries(action.payload.posts).forEach(([id, value]) => {

        if (!value.images.small || !value.images.medium || !value.images.large) {
          ids.processing.push(value.id)
        } else {
          ids.completed.push(value.id)
        }
      })

      const result = [...state, ...ids.processing].filter(item => !ids.completed.includes(item))
      const map = Object.create(null)

      result.forEach(key => {
        map[key] = true
      })

      return Object.keys(map).length === 0 ? emptyArray : Object.keys(map)
    default:
      return state
  }
}
const postsReducer = (state = initialState.watchedEntries, action) => {
  switch (action.type) {
    case types.RECEIVE_POSTS:
      return action.payload.posts.reduce((accumulator, current) => {
        accumulator[current.id] = current
        return accumulator
      }, state)
    default:
      return state
  }
}
const reducer = (state = initialState, action) => {
  console.log({oldState: state, type: action.type, payload: action.payload})

  return {
    watchedEntries: watchedEntriesReducer(state.watchedEntries, action),
    posts: postsReducer(state.posts, action),
  }
}
export {initialState, types, reducer}
