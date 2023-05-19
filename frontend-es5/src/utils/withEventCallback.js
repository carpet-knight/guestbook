import {useRef, useLayoutEffect} from 'react'

export function useEventCallback(fn) {
  const ref = useRef()
  useLayoutEffect(() => {
    ref.current = fn
  })
}

export default useEventCallback
