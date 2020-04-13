import React from 'react';
import { Container, Row } from 'reactstrap';

class SidebarWithFooter extends React.Component {
    render() {
        const { fluid, default: basic } = this.props;
        return (
            <footer className={`footer${basic ? ' footer-default' : ''}`}>
                <Container fluid={fluid}>
                    <Row className="text-center">
                        <div className="col-12">
                            <div className="credits index-footer-center">
                <span className="copyright" style={{ float: 'none' }}>
                  상호 : 주식회사 에스에스엔씨
                </span>
                            </div>
                        </div>
                    </Row>
                </Container>
            </footer>
        );
    }
}

export default SidebarWithFooter;
