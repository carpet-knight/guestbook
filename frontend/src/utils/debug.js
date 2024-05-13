import queryString from 'query-string'

export const isDebug = () => {
  const parsed = queryString.parse(document.location.search)
  return parsed.debug === 'true'
}
