import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Button } from 'reactstrap';
import moment from 'moment';

@observer
class FirewallSearchAssigneesTableBody extends Component {
    constructor() {
        super();
        this.state = {
            status: '',
            style: ''
        };
    }

    handleStatus(value) {
        switch (value) {
            case 'PENDING' :
                this.setState({status: "대기 중", style: "pending-text"});
                break;
            case 'APPROVED':
                this.setState({status: "승인 완료", style: "apporoval-text"});
                break;
            default:
                this.setState({status: "승인 완료", style: "apporoval-text"});
                break;
        }
    }

    renderTableBody = () => {
        const { store } = this.props;
        const { data } = store.detailData;
        if (data) {
            return data.assignees.map((row, key) => {
                return (
                    // <tr onClick={() => store.toggleDetailInfoModal(row.id)} className="text-center" key={row.id}>
                    <tr>
                        <td>{`${key + 1}`}</td>
                        <td>{`${row.assign.username}`}</td>
                        {/* eslint-disable-next-line no-nested-ternary */}
                        { row.status==='PENDING' ? (
                            <td className="pending-text">대기 중</td>
                        ) : row.status==='APPROVED' ? (
                            <td className="apporoval-text">승인 완료</td>
                        ) : (
                            <td className="reject-text">반려</td>
                        )}
                        {/*<td>{`${row.status}`}</td>*/}
                        {/*<td>{row.checkedIn ? row.checkedIn.location.place.address : ''}</td>*/}
                        <td>{moment(row.updateDate).format('YYYY.MM.DD dddd HH:mm:SS')}</td>
                        {/*<td>{row.status === 'SCHEDULED' ? '미방문' : '방문'}</td>*/}
                        {/*<td>{row.protocol}</td>*/}
                        {/*<td>{row.port}</td>*/}
                            { row.status==='PENDING' ? (
                                <td>
                                <Button
                                    onClick={e => store.confirmApprovalFirewall(e)}
                                    className="apporoval-button"
                                    color="warning"
                                    size="sm"
                                >
                                    승인
                               </Button>
                                {" "}
                                <Button
                                onClick={e => store.handleFireRuleCheck(e)}
                                className="reject-button"
                                color="warning"
                                size="sm"
                                >
                                반려
                                </Button>
                                </td>
                              )
                            : (
                                <td>
                                {/*<Button*/}
                                {/*    onClick={e => store.handleFireRuleCheck(e)}*/}
                                {/*    className="btn-icon btn-round"*/}
                                {/*    color="warning"*/}
                                {/*    size="sm"*/}
                                {/*>*/}
                                {/*    <i className="fa fa-remove" />*/}
                                {/*</Button>*/}
                                </td>
                            )}

                    </tr>
                );
            });
        }
        return null;
    };

    render() {
        return <>{this.renderTableBody()}</>;
    }
}

export default FirewallSearchAssigneesTableBody;