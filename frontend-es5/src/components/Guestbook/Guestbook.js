import React from 'react'

import MessageList from '../MessageList/MessageList'

import {GuestbookForm} from '../GuestbookForm/GuestbookForm'

export const GuestBookComponent = () => (
  <>
    <GuestbookForm />
    <MessageList />
  </>
)

export default GuestBookComponent
