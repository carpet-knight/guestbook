import React from 'react'
import Loader from '@jetbrains/ring-ui/components/loader/loader'

import Message from '../Message/Message'

import {requestPostById, requestPosts} from '../../requests/posts'

import {StoreContext} from '../../contexts/StoreContext'

import styles from './MessageList.module.css'

const POLLING_DELAY = 1000
const MessageList = () => {
  const [error, setError] = React.useState(null)
  const [parseErrors, setParseErrors] = React.useState([])
  const [loading, setLoading] = React.useState(true)
  const {state, actions} = React.useContext(StoreContext)

  const messagesToLoad = React.useMemo(() => state.watchedEntries, [state])
  const fetchData = React.useCallback(async () => {
    const response = await requestPosts()

    if (response.status === 'success') {
      actions.receiveEntries(response.data)
    } else {
      setError(response.message)
    }

    setLoading(false)
  }, [actions])

  React.useEffect(() => {
    const promiseList = []

    if (messagesToLoad.length === 0) {
      return
    }

    const timeout = setTimeout(() => {
      messagesToLoad.forEach(id =>
        promiseList.push(
          new Promise(async resolve => {
            const response = await requestPostById(id)
            resolve({response, id})
          }),
        ),
      )

      Promise.all(promiseList).then(entries => {
        const failItems = []
        const successItems = []

        entries.forEach(entry =>
          entry.response.status === 'success' ? successItems.push(entry) : failItems.push(entry),
        )

        if (failItems.length > 0) {
          const errors = [...parseErrors, ...failItems.map(item => item.id)]
          setParseErrors(errors)
          actions.stopEntriesWatching(errors)
        }
        actions.receiveEntries(successItems.map(item => item.response.data))
      })
    }, POLLING_DELAY)

    return () => clearTimeout(timeout)
  }, [messagesToLoad])

  React.useEffect(() => {
    fetchData()
  }, [])

  const processingErrors = React.useMemo(() => {
    if (!parseErrors || parseErrors.length === 0) {
      return null
    }

    return (
      <div className={styles.parseErrors}>
        <span>{'The next images could not be processed: '}</span>
        <br/>
        {parseErrors.map(postId => <><span key={postId}>{postId}</span><br /></>)}
      </div>
    )
  }, [state, parseErrors])

  if (loading) {
    return <Loader />
  }

  if (error) {
    return <div>{'Could not load images'}</div>
  }


  return (
    <div className={styles.messageList}>
      {processingErrors}
      {Object.values(state.posts)
        .sort((a, b) => {
          // @ts-ignore
          const dateA = new Date(a.datetime)
          // @ts-ignore
          const dateB = new Date(b.datetime)
          if (dateA > dateB) {
            return -1
          } else if (dateA < dateB) {
            return 1
          } else {
            return 0
          }
        })
        .map(post => (
          // @ts-ignore
          <Message key={post.id} {...post} />
        ))}
    </div>
  )
}

export default MessageList
