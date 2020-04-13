import React from 'react';
import { Container, Row } from 'reactstrap';

class SidebarWithoutFooter extends React.Component {
    render() {
        const { fluid } = this.props;
        return (
            <footer className="footer footer-default">
                <Container fluid={fluid}>
                    <Row className="credits justify-content-between">
            <span className="copyright">
                Seoul, Republic of Korea
            </span>
                        <span className="copyright">COPYRIGHT &copy; 2020 BY SSNC, LTD. ALL RIGHTS RESERVED.</span>
                    </Row>
                </Container>
            </footer>
        );
    }
}

export default SidebarWithoutFooter;
