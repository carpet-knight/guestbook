import React from 'react'
import moment from 'moment'
import {useMedia} from 'use-media'
import classNames from 'classnames'

import Theme from '@jetbrains/ring-ui/components/global/theme'
import Text from '@jetbrains/ring-ui/components/text/text'
import ProgressBar from '@jetbrains/ring-ui/components/progress-bar/progress-bar'

import styles from './Message.css'

const calculateTime = time =>
  moment(time)
    .startOf(time)
    .fromNow()

export const Message = ({name: author, message, images, datetime}) => {
  const desktop = useMedia({minWidth: 1100})
  const tablet = useMedia({minWidth: 600})

  const progress = React.useMemo(() => {
    const count = Object.keys(images).length
    const loadedCount = Object.values(images).filter(image => Boolean(image)).length

    return loadedCount / count
  }, [images])

  const selectedImage = React.useMemo(() => {
    if (desktop) {
      return images.large
    } else if (tablet) {
      return images.medium
    } else {
      return images.small
    }
  }, [images, desktop, tablet])

  return (
    <div
      className={classNames(styles.message, selectedImage && progress === 1 && styles.imageLoaded)}
    >
      {selectedImage && (
        <div className={styles.messageImageContainer}>
          <div className={styles.messageImageFade} />
          <img src={selectedImage} className={styles.messageImage} alt={message} />
        </div>
      )}
      <div className={styles.description}>
        <div className={styles.info}>
          <span className={styles.author}>{`${author}, `}</span>
          <span>{`${calculateTime(datetime)}`}</span>
        </div>
        <Text className={styles.text}>{message}</Text>
        {progress !== 1 && (
          <>
            <ProgressBar theme={Theme.DARK} className={styles.progress} value={progress} />
          </>
        )}
      </div>
    </div>
  )
}

export default React.memo(Message)
