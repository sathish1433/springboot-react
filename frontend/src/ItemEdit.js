import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom';
import { Button, Container, Form, FormGroup, Input, Label } from 'reactstrap';
import AppNavbar from './AppNavbar';

class ItemEdit extends Component {

    emptyItem = {
        name: '',
        colour: ''
    };

    constructor(props) {
        super(props);
        this.state = {
            item: this.emptyItem
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    async componentDidMount() {
        if (this.props.match.params.id !== 'new') {
            const item = await (await fetch(`/v1/items/${this.props.match.params.id}`)).json();
            this.setState({item: item});
        }
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let item = {...this.state.item};
        item[name] = value;
        this.setState({item});
    }

    async handleSubmit(event) {
        event.preventDefault();
        const {item} = this.state;

        await fetch('/v1/items' + (item.id ? '/' + item.id : ''), {
            method: (item.id) ? 'PUT' : 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(item),
        });
        this.props.history.push('/items');
    }

    render() {
        const {item} = this.state;
        const title = <h2>{item.id ? 'Edit Item' : 'Add Item'}</h2>;

        return <div>
           <AppNavbar/>
           <Container className="mt-4">
               {title}
               <Form onSubmit={this.handleSubmit}>
                   <FormGroup className="mb-3">
                       <Label for="name" className="bold-label">Name</Label>
                       <Input type="text" name="name" id="name" value={item.name || ''}
                              onChange={this.handleChange} autoComplete="name"/>
                   </FormGroup>
                   <FormGroup className="mb-4">
                       <Label for="colour" className="bold-label">Colour</Label>
                       <Input type="text" name="colour" id="colour" value={item.colour || ''}
                              onChange={this.handleChange} autoComplete="colour"/>
                   </FormGroup>
                   <FormGroup className="d-flex justify-content-end">
                       <Button color="primary" type="submit" className="me-2">Save</Button>
                       <Button color="secondary" tag={Link} to="/items">Cancel</Button>
                   </FormGroup>
               </Form>
           </Container>
       </div>

    }
}
export default withRouter(ItemEdit);
