import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams, useLocation } from 'react-router-dom';
import unitService from '../../../services/unit.service';
import Form from 'react-bootstrap/Form';



const UnitAddEditPage = () => {

    const [status, setStatus] = useState("Disponible");
    const [condition, setCondition] = useState("Bueno");
    const [titleUnitForm, setTitleUnitForm] = useState("Añadir unidad");
    const { id, toolId } = useParams();
    const location = useLocation();
    const [originToolId, setOriginToolId] = useState(toolId || location.state?.toolId || null);
    const [stock, setStock] = useState(1);
    const navigate = useNavigate();

    const saveUnit = async (e) => {
        e.preventDefault();

        try {
            if (id) {
                // editar
                const confirmUpdate = window.confirm("¿Esta seguro que desea actualizar?");
                if (!confirmUpdate) {
                    return;
                }
                const unit = { status, condition };
                const response = await unitService.updateUnit(id, unit);
                navigate(originToolId ? `/tools/${originToolId}` : '/tools');

            } else {
                // crear

                // 1. Update Tool Stock (REMOVED - Handled by Backend)

                // 2. Create Units
                const unitBase = { status, condition, tool: { toolId: Number(toolId) } };
                await unitService.createUnit(unitBase);
                ;

                for (let i = 1; i < stock; i++) {
                    try {
                        await unitService.createUnit(unitBase);
                    } catch (error) {
                        console.log('Error creating additional unit', error);
                    }
                }

                navigate(`/tools/${toolId}`);

            }
        } catch (error) {
            console.log('Something went wrong', error);
        }
    }

    useEffect(() => {
        if (condition === "Dañada") {
            setStatus("Dañado");
        }
    }, [condition]);

    useEffect(() => {
        if (id) {
            // editar
            setTitleUnitForm("Editar unidad");
            unitService.getUnitById(id).then(response => {
                const unitData = response.data;
                setStatus(unitData.status);
                setCondition(unitData.condition);
                // Si venimos por la ruta de edición, extraer el toolId desde la unidad cargada.
                if (unitData.tool && unitData.tool.toolId) {
                    setOriginToolId(unitData.tool.toolId);
                }
            }).catch(error => {
                console.log('Something went wrong', error);
            });
        } else {
            setTitleUnitForm("Añadir unidad");
            setStatus("Disponible");
        }
    }, [id]);





    return (
        <Form onSubmit={saveUnit}>
            <h3>{titleUnitForm}</h3>
            <Form.Group className="mb-3" controlId="formStatus">
                <Form.Label>Estado</Form.Label>
                <Form.Select
                    value={status}
                    onChange={(e) => setStatus(e.target.value)}
                    disabled={!id}
                >
                    <option value="Disponible">Disponible</option>
                    <option value="Prestado">Prestada</option>
                    <option value="En Mantenimiento">En Mantenimiento</option>
                    <option value="Dañado">Dañada</option>
                    <option value="Dado de Baja"></option>
                </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3" controlId="formCondition">
                <Form.Label>Condición</Form.Label>
                <Form.Control
                    type="text"
                    value={condition}
                    onChange={(e) => setCondition(e.target.value)}
                />
            </Form.Group>

            {!id && (
                <Form.Group className="mb-3" controlId="formStock">
                    <Form.Label>Cantidad de unidades a crear</Form.Label>
                    <Form.Control
                        type="number"
                        min="1"
                        value={stock}
                        onChange={(e) => setStock(Number(e.target.value))}
                        required
                    />
                </Form.Group>
            )}

            <button type="submit" className='outline-dark'>Guardar</button>
            {/* Usar originToolId resuelto; si no existe, enviar al listado de herramientas */}
            <Link to={originToolId ? `/tools/${originToolId}` : '/tools'} className="btn btn-secondary" style={{ marginLeft: "10px" }}>Cancelar</Link>

        </Form>

    );
}

export default UnitAddEditPage;