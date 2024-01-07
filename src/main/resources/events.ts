import "./window"

window.ipc = {}

export function addListener(event: string, callback: () => void) {
  if (window.ipc[event]) {
    window.ipc[event].callbacks.push(callback)
    return
  }

  window.ipc[event] = {
    handler: () => window.ipc[event].callbacks.forEach(cb => cb()),
    callbacks: [callback],
  }
}

export function removeListener(event: string, callback: () => void) {
  if (!window[event]) {
    return
  }
  window.ipc[event].callbacks = window.ipc[event].callbacks.filter(cb => cb != callback)
}
