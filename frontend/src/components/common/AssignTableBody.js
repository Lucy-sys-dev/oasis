import React, { Component } from 'react';
import {Button, CardLink, Col, Row} from 'reactstrap';
import {observer} from 'mobx-react';
import CustomSelect from "./CustomSelect";
import CustomInput from "./CustomInput";

@observer
class AssignTableBody extends Component {
    renderTableBody = () => {
        const { store } = this.props;
        return (
            <div className="text-center assign-search">
                <Row>
                {/*<Col sm={1}>{ }</Col>*/}
                {/*<Col sm={2}>*/}
                    <CustomSelect
                        style={{width: "42px"}}
                        // title=""
                        name="assighType"
                        data={store.assignType}
                        handleChange={e=> store.handleAssignChange(e, 'assignType', 'TYPE')}
                    />
                {/*</Col>*/}
                {/*<Col sm={3}>*/}
                    <CustomInput className="search-input" name="assignName" handleChange={e=> store.handleAssignChange(e, 'user_id', 'TEXT')} />
                {/*</Col>*/}
                {/*<Col sm={3} className="text-center search-button">*/}
                    <Button
                        onClick={e => store.handleFireRuleCheck(e)}
                        className="search-button"
                        color="warning"
                        size="md"
                    >
                        검색
                    </Button>
                {/*</Col>*/}
                {/*<Col sm={3}>*/}
                    <Button
                        onClick={e => store.handleAddAssignees(e)}
                        className="btn-icon btn-round search-button"
                        color="warning"
                        size="md"
                    >
                        <i className="fa  fa-plus" />
                    </Button>
                </Row>
                {/*</Col>*/}
            </div>
        )
    };

    // eslint-disable-next-line consistent-return
    renderAssignList = () => {
        const { store } = this.props;

        if (store.assignees) {
            return store.assignees.map((row, key) => {
                return (
                    <tr>
                        <td>{row.order}</td>
                        <td>{ }</td>
                        <td>{row.user_id}</td>
                        <td>{ }</td>
                    </tr>
                )}
            )
        }
    };

    render() {
        return (
            <>
                {this.renderTableBody()}
                {this.renderAssignList()}
            </>
        )
    }
}

export default AssignTableBody;