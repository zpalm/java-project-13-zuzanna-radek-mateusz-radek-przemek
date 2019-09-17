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
        return [
            <div className="container-fluid">
                <div className="row">
                    <div className="col-md-4">
                        <SearchByNumber update={this.updateInvoices}/>
                    </div>
                </div><br />
                <div className="row">
                    <div className="col-md-12">
                        <InvoiceList invoices={this.state.invoices} update={this.updateInvoices}/>
                    </div>
                </div>
            </div>
        ]
    }
}

class InvoiceList extends React.Component{
    render() {
        const invoices = this.props.invoices.map(i =>
            <Invoice key={i.id} invoice={i} update={this.props.update}/>
        );
        return (
            <table id="main" className="table table-bordered table-hover">
                <tbody>
                <tr className="table-info">
                    <th scope="col-4">Number</th>
                    <th scope="col-4">Seller</th>
                    <th scope="col-4">Buyer</th>
                    <th scope="col-4">Actions</th>
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

class SearchByNumber extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
          inputValue: ''
        };
    }

    updateInputValue(event) {
        this.setState({
          inputValue: event.target.value
        });
    }

    search(number) {
        if (number == '') {
            $.notify("Invoice number cannot be empty.", "error");
        } else {
            let update = this.props.update;
            axios.get('/invoices/byNumber?number=' + number, {
                    }).then(response => {
                        $.notify("Invoice found.", "success");
                        update([response.data]);
                    }).catch(function (error) {
                        if(error.response.status == 404) {
                            $.notify("No invoice found.", "error");
                            update([]);
                        } else {
                            $.notify("An error occurred during search invoice.", "error");
                        }
                    });
        }
    }

    clear() {
        client({method: 'GET', path: '/invoices'}).done(response => {
                   this.props.update(response.entity);
                   this.setState({inputValue: ''});
                   $.notify("Search results cleared.", "success");
              });
    }

    render() {
        return(
            <div className="input-group">
              <input type="text" className="form-control" placeholder="Invoice number" value={this.state.inputValue} onChange={event => this.updateInputValue(event)}/>
              <div className="input-group-append" id="button-addon4">
                <button className="btn btn-success btn-outline-secondary" type="button" onClick={() => this.search(this.state.inputValue)}>Search</button>
                <button className="btn btn-error btn-outline-secondary" type="button" onClick={() => this.clear()}>Clear</button>
              </div>
            </div>
        )
    }
}

class ShowDetailsButton extends React.Component{

     mapVatRate(vatRate){
         switch(vatRate) {
             case 'VAT_0':
                 return 0;
                 break;
             case 'VAT_5':
                 return 5;
                 break;
             case 'VAT_8':
                 return 8;
                 break;
             case 'VAT_23':
                 return 23;
                 break;
             default:
                 return 0;
                 break;
         };
     }

     showEntries() {
        let table = [];
        var entries = this.props.invoice.entries;
            for (var i = 0; i < entries.length; i++){
                var entry = entries[i];
                table.push(
                    <tr>
                        <th><b>{i + 1}</b></th>
                        <td class="text-left">{entry.description}</td>
                        <td class="text-right">{entry.price.toFixed(2)}</td>
                        <td class="text-right">{entry.quantity}</td>
                        <td class="text-right">{entry.netValue.toFixed(2)}</td>
                        <td class="text-right">{this.mapVatRate(entry.vatRate)}</td>
                        <td class="text-right">{(entry.grossValue - entry.netValue).toFixed(2)}</td>
                        <td class="text-right">{entry.grossValue.toFixed(2)}</td>
                    </tr>);
            }
        return table;
     }

    render()
     {
     return (
        <React.Fragment>
        <div class="modal fade" id={"showDetailsModal" + this.props.invoice.id} tabIndex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalTitle">Invoice number: {this.props.invoice.number}</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                    </div>
                <div class="modal-body" id="modalBody">
                    <table class="table table-bordered">
                        <thead>
                            <tr class="table-primary text-center">
                                <th></th>
                                <th>Seller</th>
                                <th>Buyer</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <th class="align-middle"><b>Name:</b></th>
                                <td class="align-middle">{this.props.invoice.seller.name}</td>
                                <td class="align-middle">{this.props.invoice.buyer.name}</td>
                            </tr>
                            <tr>
                                <th class="align-middle"><b>Address:</b></th>
                                <td class="align-middle">{this.props.invoice.seller.address}</td>
                                <td class="align-middle">{this.props.invoice.buyer.address}</td>
                            </tr>
                            <tr>
                                <th class="align-middle"><b>Tax ID:</b></th>
                                <td class="align-middle">{this.props.invoice.seller.taxId}</td>
                                <td class="align-middle">{this.props.invoice.buyer.taxId}</td>
                            </tr>
                            <tr>
                                <th class="align-middle"><b>Account number:</b></th>
                                <td class="align-middle">{this.props.invoice.seller.accountNumber}</td>
                                <td class="align-middle">{this.props.invoice.buyer.accountNumber}</td>
                            </tr>
                            <tr>
                                <th class="align-middle"><b>Phone number:</b></th>
                                <td class="align-middle">{this.props.invoice.seller.phoneNumber}</td>
                                <td class="align-middle">{this.props.invoice.buyer.phoneNumber}</td>
                            </tr>
                            <tr>
                                <th class="align-middle"><b>E-mail address:</b></th>
                                <td class="align-middle">{this.props.invoice.seller.email}</td>
                                <td class="align-middle">{this.props.invoice.buyer.email}</td>
                            </tr>
                        </tbody>
                    </table>
                    <table class="table table-bordered">
                        <thead>
                            <tr class="table-primary text-center">
                                <th></th>
                                <th class="align-middle">Item</th>
                                <th class="align-middle">Price [zł]</th>
                                <th class="align-middle">Quantity</th>
                                <th class="align-middle">Net value [zł]</th>
                                <th class="align-middle">VAT rate [%]</th>
                                <th class="align-middle">VAT value [zł]</th>
                                <th class="align-middle">Gross value [zł]</th>
                            </tr>
                        </thead>
                        <tbody>
                            {this.showEntries()}
                        </tbody>
                    </table>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                </div>
                </div>
            </div>
        </div>
            <button type="button" class="btn btn-primary" data-toggle="modal" data-target={"#showDetailsModal" + this.props.invoice.id}>
                Show details
            </button>
        </React.Fragment>
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
                    {' '}
                    <ShowDetailsButton invoice={this.props.invoice} />
                 </td>
            </tr>
        )
    }
}

ReactDOM.render(
    <App />,
    document.getElementById('react')
)
