declare global {
  interface Window {
    ipc: Ipc
  }
}

type IpcEventSet = {
  [event: string]: {
    callbacks: (() => void)[]
    handler: () => void,
  }
}

export class Ipc {
  private events: IpcEventSet

  constructor() {
    this.events = {} as IpcEventSet
  }

  addHandler(event: string, callback: () => void) {
    if (!this.events[event]) {
      this.events[event] = {
        callbacks: [],
        handler: () => {
          if (this.events[event]) {
            this.events[event].callbacks.forEach(cb => cb())
          }
        }
      }
    }

    this.events[event].callbacks.push(callback)
  }

  removeHandler(event: string, callback: () => void) {
    if (!this.events[event]) {
      return
    }

    this.events[event].callbacks = this.events[event].callbacks.filter(cb => cb != callback)
  }

  clearHandlers(event: string) {
    if (!this.events[event]) {
      return
    }

    this.events[event].callbacks = []
  }
}

window.ipc = new Ipc()