'use strict';

const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {invoices: []};
    }

    componentDidMount() {
        client({method: 'GET', path: '/invoices'}).done(response => {
            this.setState({invoices: response.entity});
        });
    }

    render() {
        return (
            <InvoiceList invoices={this.state.invoices}/>
        )
    }
}

class InvoiceList extends React.Component{
    render() {
        const invoices = this.props.invoices.map(i =>
            <Invoice key={i.id} invoice={i}/>
        );
        return (
            <table className="table table-bordered table-hover">
                <tbody>
                <tr className="table-info">
                    <th scope="col">Number</th>
                    <th scope="col">Seller</th>
                    <th scope="col">Buyer</th>
                </tr>
                {invoices}
                </tbody>
            </table>
        )
    }
}

class Invoice extends React.Component{
    render() {
        return (
            <tr>
                <td>{this.props.invoice.number}</td>
                <td>{this.props.invoice.seller.name}</td>
                <td>{this.props.invoice.buyer.name}</td>
            </tr>
        )
    }
}

ReactDOM.render(
    <App />,
    document.getElementById('react')
)
