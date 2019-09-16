'use strict';

const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
const axios = require('axios');

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {invoices: []};
        this.updateInvoices = this.updateInvoices.bind(this);
    }

    componentDidMount() {
        client({method: 'GET', path: '/invoices'}).done(response => {
            this.setState({invoices: response.entity});
        });
    }

    updateInvoices(data) {
        this.setState({invoices: data})
    }

    render() {
        return (
            <InvoiceList invoices={this.state.invoices} update={this.updateInvoices}/>
        )
    }
}

class InvoiceList extends React.Component{
    render() {
        const invoices = this.props.invoices.map(i =>
            <Invoice key={i.id} invoice={i} update={this.props.update}/>
        );
        return (
            <table className="table table-bordered table-hover">
                <tbody>
                <tr className="table-info">
                    <th scope="col">Number</th>
                    <th scope="col">Seller</th>
                    <th scope="col">Buyer</th>
                    <th scope="col">Actions</th>
                </tr>
                {invoices}
                </tbody>
            </table>
        )
    }
}

class DeleteButton extends React.Component{
    deleteInvoice(id) {
        axios.delete('/invoices/' + id, {
        }).then(response => {
            client({method: 'GET', path: '/invoices'}).done(response => {
                        this.props.update(response.entity);
                    });
            $.notify("Invoice deleted.", "success");
        }).catch(function (error) {
            $.notify("An error occurred during deleting invoice.", "error");
        });
    }

    render() {
        return(
            <button type="button" class="btn btn-danger" onClick={() => {if (window.confirm('Are you sure you want to delete this invoice?')) this.deleteInvoice(this.props.invoiceId)}}>Delete</button>
        )
    }
}

class PdfButton extends React.Component{
    getPdf(id) {
        axios.get('/invoices/' + id, {
            responseType: 'arraybuffer',
            headers: {'Accept': 'application/pdf'}
        }).then(response => {
            const blob = new Blob([response.data], {type: 'application/pdf'});
            const url = URL.createObjectURL(blob);
            let link = document.createElement('a');
            link.href = url;
            link.download = 'Invoice.pdf';
            link.click();
            $.notify("Pdf file has been downloaded.", "success");
        }).catch(function (error) {
            $.notify("An error occurred during downloading pdf file.", "error");
        });
    }

    render() {
        return(
            <button type="button" class="btn btn-success" onClick={() => this.getPdf(this.props.invoiceId)}>Pdf</button>
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
                <td>
                    <DeleteButton invoiceId={this.props.invoice.id} update={this.props.update} />
                    {' '}
                    <PdfButton invoiceId={this.props.invoice.id} />
                 </td>
            </tr>
        )
    }
}

ReactDOM.render(
    <App />,
    document.getElementById('react')
)
