export function parseDate(data) {
  const index = data.lastIndexOf(':');
  return data.substring(0, index).replace('T', ' ');
}

export function appendReply(data, depth) {
  if (depth === 1) {
    return data;
  }
  let prefix = '';
  for (let i = 1; i < depth - 1; i++) {
    prefix += '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
  }
  prefix += '↪️ '
  return prefix + data;
}
