import React, { Component } from 'react';
import { Col, FormGroup, Label, Row } from 'reactstrap';
import ReactDatetime from 'react-datetime';
import moment from 'moment';

class CustomDate extends Component {
    constructor(props) {
        super(props);
        this.isValidDate = this.isValidDate.bind(this);
    }

    isValidDate(current) {
        // eslint-disable-next-line react/destructuring-assignment
        const startTime = ReactDatetime.moment(this.props.start);
        const endTime = ReactDatetime.moment();
        return current.isAfter(startTime) && current.isBefore(endTime);
    }

    render() {
        const { prevDate, endDate, handlePrevDateChange, handleEndDateChange, yesterday } = this.props;

        return (
            <>
                <Col>
                    <Row>
                        <Col xs="5" className="datetime-custom-left">
                            <FormGroup>
                                <ReactDatetime
                                    inputProps={{
                                        name: 'prevDate',
                                        className: 'form-control datetime-custom text-center',
                                        value: prevDate,
                                        readOnly: true,
                                    }}
                                    defaultValue={prevDate}
                                    dateFormat="YYYY-MM-DD"
                                    onChange={async e => {
                                        await handlePrevDateChange(e);
                                    }}
                                    locale="ko"
                                    // isValidDate={current => {
                                    //     return yesterday ? current.isBefore(moment().subtract(1, 'days')) : current.isBefore(moment());
                                    // }}
                                    timeFormat={false}
                                    closeOnSelect
                                />
                            </FormGroup>
                        </Col>
                        <Label xs="1" className="search-label text-center datetime-custom-center dateLabel">
                            ~
                        </Label>
                        <Col xs="5" className="datetime-custom-right">
                            <FormGroup>
                                <ReactDatetime
                                    inputProps={{
                                        name: 'endDate',
                                        className: 'form-control datetime-custom text-center',
                                        value: endDate,
                                        readOnly: true,
                                    }}
                                    defaultValue={endDate}
                                    dateFormat="YYYY-MM-DD"
                                    onChange={async e => {
                                        await handleEndDateChange(e);
                                    }}
                                    locale="ko"
                                    // isValidDate={current => { return current.isAfter( moment().subtract(1, 'day') );
                                    //     // return yesterday ? current.isBefore(moment().subtract(1, 'days')) : current.isBefore(moment());
                                    // }}
                                    // isValidDate={this.isValidDate}
                                    timeFormat={false}
                                    closeOnSelect
                                />
                            </FormGroup>
                        </Col>
                    </Row>
                </Col>
            </>
        );
    }
}

export default CustomDate;
