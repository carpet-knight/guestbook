const checkImgUrl = url => {
  const anchor = document.createElement('a')
  anchor.href = url
  return /\.(gif|jpg|jpeg|tiff|png)$/i.test(anchor.pathname)
}

export default checkImgUrl
