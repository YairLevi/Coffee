type IpcEventSet = {
  [event: string]: {
    callbacks: (() => void)[]
    handler: () => void,
  }
}

class Ipc {
  private events: IpcEventSet

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

export const ipc = new Ipc()
