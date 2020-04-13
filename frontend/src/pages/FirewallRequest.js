import React, { Component } from 'react';
import {Button, Col, Container, Row, Table, Card, CardBody} from 'reactstrap';
import { inject, observer } from 'mobx-react';
import FirewallRequestBodyCard from "../components/firewall-request/FirewallRequestBodyCard";
import TableHeaders from "../components/common/TableHeaders";
import AssignTableBody from "../components/common/AssignTableBody";

@inject('firewallStore')
@observer
class FirewallRequest extends Component {
    async componentDidMount() {
        const { firewallStore, onUpdate, history } = this.props;
        document.title = 'OASIS. Web - 방화벽 > 방화벽 신청';
        onUpdate();
    }

    render() {
        const { firewallStore } = this.props;
        return (
            <div className="admin-page">
                <Container fluid>
                    <Row>
                        <Col style={{paddingLeft:"22px"}}>
                            <h3>
                                {'방화벽 > 방화벽 신청'}
                            </h3>
                        </Col>
                    </Row>
                    <Row>
                        <FirewallRequestBodyCard
                            store={firewallStore}
                            tableHeader={['주소 유형', '출발지 주소', '주소 유형', '목적지 주소', '프로토콜', '포트', '허용여부', '기간', '요청내용', '액션']}
                        />
                    </Row>
                    <Row>
                        <Col style={{paddingLeft:"22px"}}>
                            <h4>
                                결재선 지정
                            </h4>
                        </Col>
                    </Row>
                    <Row style={{paddingLeft: "22px"}}>
                        <Card>
                            <CardBody className="body-card-2">
                        {/*<Col className="table-body">*/}
                            {/*<Table className="assign-table custom-table-clickable" bordered striped responsive>*/}
                            {/*    /!*<TableHeaders tableHeader={['No', '구분', '이름', 'action']} />*!/*/}
                            {/*    <tbody>*/}
                                <AssignTableBody store={firewallStore} removeRow={this.removeRow} />
                            {/*    </tbody>*/}
                            {/*</Table>*/}
                            {/*<ApllyForm />*/}
                        {/*</Col>*/}
                            </CardBody>
                        </Card>
                    </Row>
                    <div className="modal-footer">
                        <div className="right-side">
                            <Button className="btn-link firewall-request" color="default" type="button" onClick={e => firewallStore.approvalFirewall(e)}>
                                요청
                            </Button>
                        </div>
                        {/*<div className="divider" />*/}
                        <div className="left-side">
                            <Button className="btn-link firewall-request" type="button" onClick={firewallStore.toggleDetailInfoModal}>
                                취소
                            </Button>
                        </div>
                    </div>
                </Container>
            </div>
        )
    }
}

export default FirewallRequest;