import React, { Component } from 'react';
import './App.css';
import AppNavbar from './AppNavbar';
import { Link } from 'react-router-dom';
import { Button, Container } from 'reactstrap';
import reactLogo from './react-logo.png';
import springBootLogo from './spring-boot-logo.png';

class Home extends Component {
    render() {
        return (
            <div>
                <AppNavbar/>
                <Container fluid>
                    <h3 className="mb-0">React & Spring Boot Demo</h3>
                    <div className="d-flex justify-content-start align-items-center mt-4 mb-3" style={{ paddingLeft: '30px' }}>
                        <img src={reactLogo} alt="React Logo" className="me-5" style={{ height: '100px' }}/>
                        <img src={springBootLogo} alt="Spring Boot Logo" className="me-5" style={{ height: '100px' }}/>
                    </div>
                    <div className="d-flex justify-content-between align-items-center mt-4 mb-3">
                        Demonstrating a React front item to list, add, edit and delete items, with Spring Boot applicaton using a Postgres database to persist the items.
                    </div>
                    <Button color="primary" tag={Link} to="/items">List Items</Button>
                </Container>
            </div>
        );
    }
}
export default Home;
