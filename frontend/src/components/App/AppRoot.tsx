import React from 'react'
import Header, {Logo} from '@jetbrains/ring-ui/components/header/header'
import Link from '@jetbrains/ring-ui/components/link/link'
import TeamCityLogo from '@jetbrains/logos/teamcity/teamcity'
import GuestBook from '../Guestbook/Guestbook'
import {StoreProvider} from '../../contexts/StoreContext'

import styles from './App.module.css'

const AppRoot = () => (
  <StoreProvider>
    <>
      <Header className={styles.header}>
        <Link className={styles.logo} href="/">
          {() => <TeamCityLogo />}
        </Link>
      </Header>
      <div className={styles.appContent}>
        <GuestBook />
      </div>
    </>
  </StoreProvider>
)

export default AppRoot
