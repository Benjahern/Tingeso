import React from "react";
import { Container, Row, Col, Card } from "react-bootstrap";
import ClientsFine from "../common/ClientsFine";
import ToolRanking from "../common/ToolRanking";
import ActiveLoans from "../common/ActiveLoans";

const HomePage = () => (
  <Container fluid className="p-3">
    <Row className="mb-4 g-4">
      <Col xl={6} lg={6} md={12}>
        <Card className="h-100 shadow-sm" style={{width: "fit-content"}}>
          <Card.Body>
            <ActiveLoans />
          </Card.Body>
        </Card>
      </Col>
      <Col xl={6} lg={6} md={12}>
        <Card className="h-100 shadow-sm" style={{width: "fit-content"}}>
          <Card.Body>
            <ClientsFine />
          </Card.Body>
        </Card>
      </Col>
    </Row>
    <Row>
      <Col>
        <Card style={{width: "fit-content"}}>
          <Card.Body>
            <ToolRanking />
          </Card.Body>
        </Card>
      </Col>
    </Row>
  </Container>
);

export default HomePage;
