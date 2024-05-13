import {fetch} from 'whatwg-fetch'
// eslint-disable-next-line import/extensions,import/no-unresolved
import env from 'env'

const endpoint = `${env.api}/entries`

const processResponse = response => response.json()

export const uploadPosts = async ({name, imageUrl, message}) => {
  const response = await fetch(endpoint, {
    method: 'POST',
    headers: {'Content-type': 'application/json'},
    body: JSON.stringify({
      author: name,
      imageUrl,
      message,
    }),
  })
  return await processResponse(response)
}

// data: {message: "Can't get input stream from URL!"}
// message: "Can't get input stream from URL!"
// status: "error"
export const requestPosts = async () => {
  const response = await fetch(endpoint, {
    method: 'GET',
    headers: {
      'Content-type': 'application/json',
    },
  })

  const formattedData = await processResponse(response)

  return {
    status: formattedData.status,
    data: formattedData.data.messages,
  }
}

export const requestPostById = async (id) => {
  const response = await fetch(`${endpoint}/${id}`, {
    method: 'GET',
    headers: {
      'Content-type': 'application/json',
    },
  })

  return await processResponse(response)
}
