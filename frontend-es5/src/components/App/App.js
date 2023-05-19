import React from 'react'
import {render} from 'react-dom'
import RedBox from 'redbox-react'

import AppRoot from './AppRoot'
import styles from './App.css'

const appEl = document.querySelector('.app-root')
const rootEl = document.createElement('div')

appEl.classList.add(styles.appRoot)
rootEl.classList.add(styles.rootElement)

let renderApp = () => render(<AppRoot className={styles.appRoot}/>, rootEl)

/* Hot Replacement support, won't be bundled to production */
/* eslint-disable modules/no-exports-typo */
if (module.hot) {
  const renderAppHot = renderApp
  const renderError = error => {
    render(<RedBox error={error} />, rootEl)
  }

  renderApp = () => {
    try {
      renderAppHot()
    } catch (error) {
      renderError(error)
    }
  }

  module.hot.accept('./AppRoot', () => {
    setTimeout(renderApp)
  })
}

renderApp()
appEl.appendChild(rootEl)
