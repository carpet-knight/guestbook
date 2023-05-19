import React from 'react'
import Header, {Logo} from '@jetbrains/ring-ui/components/header/header'
import Link from '@jetbrains/ring-ui/components/link/link'
import jbLogo from '@jetbrains/logos/jetbrains/jetbrains.svg'

import GuestBook from '../Guestbook/Guestbook'
import {StoreProvider} from '../../contexts/StoreContext'

import styles from './App.css'

const AppRoot = () => (
  <StoreProvider>
    <>
      <Header className={styles.header}>
        <Link href="/">
          <Logo glyph={jbLogo} size={Logo.Size.Size48} />
        </Link>
        <Link href="/">{'Hello, Marco!'}</Link>
      </Header>
      <div className={styles.appContent}>
        <GuestBook />
      </div>
    </>
  </StoreProvider>
)

export default AppRoot
