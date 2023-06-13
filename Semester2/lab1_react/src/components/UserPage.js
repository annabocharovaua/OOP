import React, {useState} from "react";
import {
    addServiceToClient,
    addUserPayment,
    getAllServices,
    getUserAddedServices,
    getUserPaidServices
} from "../DBRequests";
import Service from "../Service";
import {useAuth0} from "@auth0/auth0-react";
import LoadingComponent from "./LoadingComponent";

function formatPhoneNumber(phoneNumberString) {
    if(phoneNumberString.length!=10){ return phoneNumberString; }
    var cleaned = ('' + phoneNumberString).replace(/\D/g, '');
    var match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);
    if (match) {
        return '(' + match[1] + ') ' + match[2] + '-' + match[3];
    }
    return null;
}

function GetListServices(client, allServices, userAddedServices, userPaidServices){
    const listServices = allServices.map(service =>{
            const isAdded = userAddedServices.some(obj => obj.id === service.id);
            const isPaid = userPaidServices.some(obj => obj.id === service.id);

            let addDiv;
            let payDiv;

            if(!isAdded){
                addDiv = <button
                    className="btn btn-success btn-circle btn-sm"
                    onClick={()=> addServiceToClient(client.id, service.id).then((r)=>{window.location.reload(false);})}
                > <p className="plus-text">+</p></button>;
            }
            else{
                if(!isPaid){
                    payDiv = <button
                        className="btn btn-primary"
                        onClick={()=> addUserPayment(client.id, service.id).then((r)=>{window.location.reload(false);})}
                    > Pay {service.price}$ </button>;
                }
            }

            let elemClass = "list-group-item d-flex justify-content-between align-items-center ";

            if(isAdded && isPaid){
                elemClass += "list-group-item-success";
            }else if(isAdded && !isPaid){
                elemClass += "list-group-item-danger";
            }

            let elem = <li class={elemClass}>
                <div className="ms-2 me-auto">
                    <div className="fw-bold">{service.name}</div>
                    Price: {service.price}$
                </div>
                {addDiv} {payDiv}
            </li>;

            return elem;
        }
    );

    return listServices;
}

const UserPage = ({client})=>{
    const { user} = useAuth0();
    let [allServices, setAllServices] = useState(null);
    let [userAddedServices, setUserAddedServices] = useState(null);
    let [userPaidServices, setUserPaidServices] = useState(null);

    if(allServices==null){
        getAllServices().then(r=>{
            let services = r;
            let servicesArray = [];
            for(let i = 0; i < services.length; i++){
                servicesArray.push(Service.from(services[i]));
            }
            setAllServices(servicesArray);
        });
    }

    if(userAddedServices==null){
        getUserAddedServices(client.id).then(r=>{
            let services = r;
            let servicesArray = [];
            for(let i = 0; i < services.length; i++){
                servicesArray.push(Service.from(services[i]));
            }
            setUserAddedServices(servicesArray);
        });
    }

    if(userPaidServices==null){
        getUserPaidServices(client.id).then(r=>{
            let services = r;
            let servicesArray = [];
            for(let i = 0; i < services.length; i++){
                servicesArray.push(Service.from(services[i]));
            }
            setUserPaidServices(servicesArray);
        });
    }

    if(allServices==null || userAddedServices==null || userPaidServices==null) return <LoadingComponent/>

    const listServices = GetListServices(client, allServices, userAddedServices, userPaidServices)

    return (
        <div class="d-flex flex-user">
                <div className="align-self-start col-lg-1 card-body text-center shadow-sm p-3 mb-5 bg-white rounded">
                    <img src={user.picture} alt="avatar"
                         className="rounded-circle img-fluid img-profile"/>
                    <h5 className="my-3">{user.nickname}</h5>
                    <p className="text-muted mb-1">{client.email}</p>
                    <p className="text-muted mb-1"> {formatPhoneNumber(client.phonenumber.toString())}</p>
                </div>
                <div className="col-lg-8 card-body text-center shadow-sm p-3 mb-5 bg-white rounded">
                    <h3 className="my-3">
                        <center>Services</center>
                    </h3>
                    <ul className="list-group list-group-light">
                        {listServices}
                    </ul>
                </div>
        </div>
    )
}

export default UserPage;