interface IPC {
  events: {
    [event: string]: {
      handler: () => void
      callbacks: (() => void)[]
    }
  }
  on: (event: string, callback: () => void) => void
}

function on(event: string, callback: () => void) {
  if (ipc[event]) {
    ipc[event].callbacks.push(callback)
    return
  }

  ipc.events[event] = {
    handler: () => ipc.events[event].callbacks.forEach(cb => cb()),
    callbacks: [callback],
  }
}

export const ipc: IPC = {
  on: on,
  events: {},
}
