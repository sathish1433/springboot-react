import React, { Component } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';
import AppNavbar from './AppNavbar';
import { Link } from 'react-router-dom';

class ItemList extends Component {

    constructor(props) {
        super(props);
        this.state = {items: []};
        this.remove = this.remove.bind(this);
    }

    componentDidMount() {
        fetch('/v1/items')
            .then(response => response.json())
            .then(data => this.setState({items: data.itemResponses}));
    }

    async remove(id) {
        await fetch(`/v1/items/${id}`, {
            method: 'DELETE',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(() => {
            let updatedItems = [...this.state.items].filter(i => i.id !== id);
            this.setState({items: updatedItems});
        });
    }

    render() {
        const {items, isLoading} = this.state;

        if (isLoading) {
            return <p>Loading...</p>;
        }

        const itemList = items.map(item => {
            return <tr key={item.id}>
                <td style={{whiteSpace: 'nowrap'}}>{item.name}</td>
                <td>{item.colour}</td>
                <td>
                    <ButtonGroup>
                        <Button size="sm" color="primary" tag={Link} to={"/items/" + item.id} style={{ width: '100px', marginRight: '4%' }} className="rounded">Edit</Button>
                        <Button size="sm" color="danger" onClick={() => this.remove(item.id)} style={{ width: '100px' }} className="rounded">Delete</Button>
                    </ButtonGroup>
                </td>
            </tr>
        });

        return (
            <div>
                <AppNavbar/>
                <Container fluid>
                    <div className="d-flex justify-content-between align-items-center mb-3">
                        <h3 className="mb-0">Items</h3>
                        <Button color="success" tag={Link} to="/items/new">Add Item</Button>
                    </div>
                    <Table className="mt-4">
                        <thead>
                        <tr>
                            <th width="30%">Name</th>
                            <th width="30%">Colour</th>
                            <th width="40%">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {itemList}
                        </tbody>
                    </Table>
                </Container>
            </div>
        );
    }
}
export default ItemList;
