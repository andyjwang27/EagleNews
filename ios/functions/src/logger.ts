/* eslint-disable require-jsdoc */
export function logInfo(message: string, data?: any) {
  console.log(`[INFO] ${message}`, data ?? "");
}

export function logWarning(message: string, data?: any) {
  console.warn(` [WARN] ${message}`, data ?? "");
}

export function logError(message: string, error?: any) {
  console.error(` [ERROR] ${message}`, error ?? "");
}
