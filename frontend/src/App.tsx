import './App.css'
import {useEffect} from "react";

function App() {
  useEffect(() => {
    window["echo"]()
  }, [])

  return (
    <>
      <button onClick={() => window["echo"]()}>click</button>
    </>
  )
}

export default App
