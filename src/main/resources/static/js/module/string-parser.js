export function parseDate(data) {
  const index = data.lastIndexOf(':');
  return data.substring(0, index).replace('T', ' ');
}
