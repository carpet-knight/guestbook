const checkImageExistence = url =>
  new Promise(resolve => {
    if (!url) {
      return resolve(false)
    }

    const imgElement = document.createElement('img')
    imgElement.src = url

    imgElement.onload = () => resolve(true)
    imgElement.onerror = () => resolve(false)

    return true
  })

export default checkImageExistence
