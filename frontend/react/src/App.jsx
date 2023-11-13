import SidebarWithHeader from "./components/shared/SideBar.jsx";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import {Wrap, WrapItem, Spinner, Text} from "@chakra-ui/react";
import CardWithImage from "./components/Card.jsx";

const App = () => {
    const[customers, setCustomers] = useState([]);
    const[loading, setLoading] = useState(false);

    useEffect(() => {
        setLoading(true);
        getCustomers().then(res => {
            setCustomers(res.data);
        }).catch(e => {
            console.log(e);
        }).finally(() => {
            setLoading(false);
        });
    }, []);

    if (loading) {
        return (
            <SidebarWithHeader>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>
        )
    }

    if (customers.length <= 0) {
        <SidebarWithHeader>
            <Text>No customers available</Text>
        </SidebarWithHeader>
    }

    return (
        <SidebarWithHeader>
            <Wrap justify={"center"} spacing={"30px"}>
                {customers.map((customer, index) => (
                    <WrapItem>
                        <CardWithImage {...customer}></CardWithImage>
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    )
}

export default App
