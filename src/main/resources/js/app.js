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

class ShowDetailsButton extends React.Component{
    boom() {
                document.getElementById('modal-text').innerHTML = "Hello!";
    }

    fetchInvoice(id) {
        fetch('/invoices/' + id)
        .then(response => {return response.json()})
        .then(function(data) {
            let output = "<table class=\"table table-bordered\" style=\"width:100%\"><thead><tr class=\"table-primary\"><th></th><th>Seller</th><th>Buyer</th></tr></thead><tbody>";
            output += "<tr><th><b>Name:</b><br></th>";
            output += "<td>" + data.seller.name + "</td>";
            output += "<td>" + data.buyer.name + "</td></tr>";
            output += "<tr><th><b>Address:</b><br></th>";
            output += "<td>" + data.seller.address + "</td>";
            output += "<td>" + data.buyer.address + "</td></tr>";
            output += "<tr><th><b>Tax ID:</b><br></th>";
            output += "<td>" + data.buyer.taxId + "</td>";
            output += "<td>" + data.buyer.taxId + "</td></tr>";
            output += "<tr><th><b>Account number:</b><br></th>";
            output += "<td>" + data.buyer.taxId + "</td>";
            output += "<td>" + data.buyer.taxId + "</td></tr>";
            output += "<tr><th><b>Phone number:</b><br></th>";
            output += "<td>" + data.buyer.phoneNumber + "</td>";
            output += "<td>" + data.buyer.phoneNumber + "</td></tr>";
            output += "<tr><th><b>E-mail address:</b><br></th>";
            output += "<td>" + data.buyer.email + "</td>";
            output += "<td>" + data.buyer.email + "</td></tr>";
            output += "</tbody></table>";
            output += "<table class=\"table table-bordered\" style=\"width:100%\"><thead><tr class=\"table-primary text-center\"><th></th><th class=\"align-middle\">Item</th><th class=\"align-middle\">Price [zł]</th><th class=\"align-middle\">Quantity</th><th class=\"align-middle\">Net value [zł]</th><th class=\"align-middle\">VAT rate [%]</th><th class=\"align-middle\">VAT value [zł]</th><th class=\"align-middle\">Gross value [zł]</th></tr></thead><tbody>";
            var entries = data.entries;
            for (var i = 0; i < entries.length; i++){
              output += "<tr><th><b>" + (i+1) + "</b><br></th>";
              var entry = entries[i];
              output += "<td style=\"text-align:left\">" + entry.description + "</td>";
              output += "<td style=\"text-align:right\">" + entry.price + "</td>";
              output += "<td style=\"text-align:right\">" + entry.quantity + "</td>";
              output += "<td style=\"text-align:right\">" + entry.netValue + "</td>";
              switch(entry.vatRate) {
                case 'VAT_0':
                    output += "<td style=\"text-align:right\">0</td>";
                    break;
                case 'VAT_5':
                    output += "<td style=\"text-align:right\">5</td>";
                    break;
                case 'VAT_8':
                    output += "<td style=\"text-align:right\">8</td>";
                    break;
                case 'VAT_23':
                    output += "<td style=\"text-align:right\">23</td>";
                    break;
              }
              output += "<td style=\"text-align:right\">" + (entry.grossValue - entry.netValue) + "</td>";
              output += "<td style=\"text-align:right\">" + entry.grossValue + "</td></tr>";
            }
            output += "</tbody></table>"
            document.getElementById('modalTitle').innerHTML = "Invoice number: " + data.number;
            document.getElementById('modalBody').innerHTML = output;
        })
    }

    render() {
        return (
            <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#exampleModal" onClick={() => this.fetchInvoice(this.props.id)}>
                Show details
            </button>
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
