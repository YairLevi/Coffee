interface Window {
  ipc: {
    [event: string]: {
      handler: () => void;
      callbacks: (() => void)[];
    };
  };
}