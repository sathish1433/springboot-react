import React, { Component } from 'react';
import './App.css';
import Home from './Home';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import ItemList from './ItemList';
import ItemEdit from "./ItemEdit";

class App extends Component {
  render() {
    return (
        <Router>
          <Switch>
            <Route path='/' exact={true} component={Home}/>
            <Route path='/items' exact={true} component={ItemList}/>
            <Route path='/items/:id' component={ItemEdit}/>
          </Switch>
        </Router>
    )
  }
}

export default App;
