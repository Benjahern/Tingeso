import React, { useState, useEffect } from 'react';
import { Container, Form, Button, Alert } from 'react-bootstrap';
import storeService from '../../services/store.service';

const SettingsPage = () => {
  const [dailyFine, setDailyFine] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);
  const storeId = 1; 

  useEffect(() => {
    loadStore();
  }, []);

  const loadStore = async () => {
    try {
      const response = await storeService.getStoreById(storeId);
      setDailyFine(response.data.dailyFine);
    } catch (error) {
      setMessage({ type: 'danger', text: 'Error al cargar la configuración' });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      await storeService.updateDailyFine(storeId, dailyFine);
      setMessage({ type: 'success', text: 'Multa actualizada correctamente' });
    } catch (error) {
      setMessage({ type: 'danger', text: 'Error al actualizar' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container className="mt-4">
      <h2>Configuración</h2>
      
      {message && (
        <Alert variant={message.type} onClose={() => setMessage(null)} dismissible>
          {message.text}
        </Alert>
      )}

      <Form onSubmit={handleSubmit} className="mt-3">
        <Form.Group className="mb-3">
          <Form.Label>Multa Diaria ($)</Form.Label>
          <Form.Control
            type="number"
            value={dailyFine}
            onChange={(e) => setDailyFine(e.target.value)}
            placeholder="Ingrese la multa diaria"
            required
          />
        </Form.Group>

        <Button variant="primary" type="submit" disabled={loading}>
          {loading ? 'Guardando...' : 'Guardar'}
        </Button>
      </Form>
    </Container>
  );
};

export default SettingsPage;
