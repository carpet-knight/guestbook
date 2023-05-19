import React from 'react'
import {Formik, useField} from 'formik'

import Input from '@jetbrains/ring-ui/components/input/input'
import Button from '@jetbrains/ring-ui/components/button/button'
import ButtonSet from '@jetbrains/ring-ui/components/button-set/button-set'

import {uploadPosts} from '../../requests/posts'

import {StoreContext} from '../../contexts/StoreContext'

import checkImageExistence from '../../utils/checkImageExistence'

import styles from './GuestbookForm.css'

import {fieldNames} from './GuestbookForm.constants'

const validateName = value => (value ? undefined : 'Name should not be empty')
const validateMessage = value => (value ? undefined : 'Message should not be empty')
const validateImage = async value => (await checkImageExistence(value)) ? undefined : 'Could not load the image'

const AdaptedField = ({label, validate, ...props}) => {
  const [field, meta] = useField({...props, validate})
  return (
    <div className={styles.inputWrapper}>
      <Input
        label={label}
        name={field.name}
        error={meta.touched ? meta.error : null}
        {...field}
        {...props}
      />
    </div>
  )
}

export function GuestbookForm() {
  const {actions} = React.useContext(StoreContext)
  const [writing, setWriting] = React.useState(false)
  const [submitError, setSubmitError] = React.useState(null)

  const onStartWriting = React.useCallback(() => setWriting(true), [])
  const onCancel = React.useCallback(() => setWriting(false))
  const onSubmit = React.useCallback(() => setWriting(false))

  const handleSubmit = React.useCallback(async formData => {
    const response = await uploadPosts(formData)

    if (response.status === 'success') {
      actions.startEntryWatching({entry: formData, entryId: response.data.entryId})
      onSubmit()
    } else {
      setSubmitError(response.data.message)
      console.log('Error response: ', response)
    }
  })

  const cancelHandle = React.useCallback(() => onCancel(), [])

  if (writing !== true) {
    return (
      <Button primary onClick={onStartWriting}>
        {'Leave us a message!'}
      </Button>
    )
  }

  return (
    <div className={styles.form}>
      <Formik
        initialValues={{
          [fieldNames.name]: '',
          [fieldNames.imageUrl]: '',
          [fieldNames.message]: '',
        }}
        onSubmit={handleSubmit}
      >
        {props => (
          <>
            <form onSubmit={props.handleSubmit}>
              <AdaptedField validate={validateName} name={fieldNames.name} label={'Name'} />
              <AdaptedField
                validate={validateImage}
                name={fieldNames.imageUrl}
                label={'Image URL'}
              />
              <AdaptedField
                validate={validateMessage}
                name={fieldNames.message}
                label={'Message'}
              />
              <ButtonSet>
                <Button type={'submit'} primary>
                  {'Post!'}
                </Button>
                <Button onClick={cancelHandle}>{'Cancel'}</Button>
              </ButtonSet>
              {submitError && <div className={styles.submitError}>{submitError}</div>}
            </form>
            <div className={styles.preview}>
              {!props.errors[fieldNames.imageUrl] && (
                <img
                  alt={props.values[fieldNames.message]}
                  src={props.values[fieldNames.imageUrl]}
                  className={styles.image}
                />
              )}
            </div>
          </>
        )}
      </Formik>
    </div>
  )
}

GuestbookForm.propTypes = {}
