import SidebarWithHeader from "./shared/SideBar.jsx";
import {useEffect} from "react";
import {getCustomers} from "./services/client.js";

const App = () => {
  useEffect(() => {
    getCustomers().then(res => {
      console.log(res);
    }).catch(e => {
      console.log(e);
    });
  }, []);

  return (
      <SidebarWithHeader></SidebarWithHeader>
  )
}

export default App
