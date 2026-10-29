/* eslint-disable require-jsdoc */
export function isPast(dateSeconds: number, now: Date) {
  return new Date(dateSeconds * 1000) < now;
}
