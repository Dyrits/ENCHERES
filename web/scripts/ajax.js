const ajax = async (method, endpoint = "", data = "") => {
    const parameters = { method: method, headers: {'Accept': 'application/json'} }
    console.log(endpoint);
    if (data) {
        parameters.headers['Content-type'] = 'application/json';
        parameters.body = JSON.stringify(data);
    }
    const response = await fetch("http://localhost:8080/api/" + endpoint, parameters);
    return await response.json();
}

const getData = async (endpoint) => { return await ajax("GET", endpoint); }

const deleteData = async (endpoint) => { await ajax("DELETE", endpoint); }

const addData = async (endpoint, data) => { await ajax("POST", endpoint, data); }

const updateData = async (endpoint, data) => { await ajax("PUT", endpoint, data); }