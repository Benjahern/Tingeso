import { useEffect, useState } from "react";
import React from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import toolService from "../../../services/tool.service";
import { Form, InputGroup } from "react-bootstrap";
import unitService from "../../../services/unit.service";
import httpClient from '../../../http-common';
import Button from 'react-bootstrap/Button';
import { useKeycloak } from "@react-keycloak/web";

const API_BASE = (httpClient.defaults.baseURL || '').replace(/\/api\/?$/, '');

const ToolAdd = () => {
    const [toolName, setName] = useState("");
    const [description, setDescription] = useState("");
    const [replacementValue, setReplacementValue] = useState(0);
    const [category, setCategory] = useState("");
    const [stock, setStock] = useState(1);
    const [dailyPrice, setDailyPrice] = useState(0);
    const [imagePath, setImagePath] = useState("");
    const [imageFile, setImageFile] = useState(null);
    const [imagePreview, setImagePreview] = useState("");
    const [loading, setLoading] = useState(false);

    const { keycloak } = useKeycloak();
    const isAdmin = keycloak?.hasRealmRole("ADMIN") || false;

    const [titleToolForm, setTitleToolForm] = useState("Añadir nueva herramienta");

    const [originalTool, setOriginalTool] = useState(null);

    const navigate = useNavigate();
    const { id } = useParams();

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            if (file.type.startsWith("image/")) {
                setImageFile(file);
                setImagePreview(URL.createObjectURL(file));
            } else {
                alert("Por favor seleccione un archivo de imagen valido.");
                e.target.value = "";
            }
        }
    };

    const saveTool = async (e) => {
        e.preventDefault();
        setLoading(true)
        try {
            const tool = { toolName, toolDescription: description, replacementValue, category, stock, dailyPrice };


            // debug: removed verbose logs

            if (id) { //editar
                const confirmUpdate = window.confirm("¿Esta seguro que desea actualizar?");
                if (!confirmUpdate) {
                    setLoading(false);
                    return;
                }

                console.log("Saving Tool. Mode: EDIT. ID:", id);
                console.log("Tool Data:", tool);

                // IMPORTANT: When creating new units, we do NOT want to overwrite the stock with the 'target' stock value here using updateTool.
                // The UnitService backend now auto-increments stock for each unit created.
                // If we send `stock: 20` here, and then create 5 units, the backend will do 20 -> 21 -> 22... -> 25.
                // So, we update everything EXCEPT stock (pass the ORIGINAL stock).
                // But wait, if we only updated name/description, stock shouldn't change. 
                // We'll trust the loop below to add the necessary stock.

                const toolToUpdate = { ...tool, stock: originalTool.stock };

                console.log("Calling toolService.updateTool (preserving original stock)...");
                await toolService.updateTool(id, toolToUpdate);
                console.log("toolService.updateTool returned successfully.");

                // If stock increased, create new units
                if (originalTool && stock > originalTool.stock) {
                    const quantityToAdd = stock - originalTool.stock;
                    console.log(`Adding ${quantityToAdd} new units for stock increase...`);

                    // We need the updated tool data schema for unit creation
                    // We can reuse 'originalTool' merged with basic updates, but keep original stock or let backend handle it.
                    const updatedToolMock = { ...originalTool, ...tool, toolId: id };

                    for (let i = 0; i < quantityToAdd; i++) {
                        try {
                            const unitData = {
                                tool: updatedToolMock,
                                status: "Disponible",
                                condition: "Bueno"
                            };
                            await unitService.createUnit(unitData);
                        } catch (unitError) {
                            console.error(`Error creating additional unit ${i + 1}:`, unitError);
                        }
                    }
                }

                //Imagen nueva
                if (imageFile) {
                    await toolService.uploadImage(id, imageFile);
                }
                navigate("/inventory");
            } else {


                // Create tool with initial stock 0.
                // The UnitService backend will auto-increment stock for each unit created in the loop below.
                const toolToCreate = { ...tool, stock: 0 };
                const response = await toolService.create(toolToCreate);
                const createdTool = response.data;
                // tool added successfully

                if (imageFile) {
                    await toolService.uploadImage(response.data.toolId, imageFile)
                }

                console.log(`Creando ${stock} unidades...`);
                for (let i = 0; i < stock; i++) {
                    try {
                        const unitData = {
                            tool: createdTool, // Objeto completo de la herramienta
                            status: "Disponible",
                            condition: "Bueno"
                        };

                        console.log(`Creando unidad ${i + 1}:`, unitData);
                        const unitResponse = await unitService.createUnit(unitData);
                        console.log(`Unidad ${i + 1} creada:`, unitResponse.data);
                    } catch (unitError) {
                        console.error(`Error al crear unidad ${i + 1}:`, unitError);
                        console.error("Respuesta del error:", unitError.response?.data);
                        if (unitError.response && (unitError.response.status === 404 || unitError.response.status === 500)) {
                            alert(`Error al crear unidad ${i + 1}. Posible causa: El usuario actual no tiene un perfil de Trabajador asociado para el registro en Kardex.`);
                        }
                    }
                }


                navigate("/inventory")
            }
        } catch (error) {
            console.log("Something went wrong", error);
            alert("Error al guardar la herramienta. Por favor intenta nuevamente.");

        } finally {
            setLoading(false);
        }

    };



    useEffect(() => {
        if (!isAdmin && !id) {
            alert("No tienes permisos para añadir herramientas.");
            navigate("/inventory");
        }
        if (id) {
            //Editar
            setTitleToolForm("Editar herramienta");
            console.log("El id de la herramienta es: " + id);
            toolService.get(id).then(response => {
                setName(response.data.toolName);
                setDescription(response.data.description);
                setReplacementValue(response.data.replacementValue);
                setCategory(response.data.category);
                setStock(response.data.stock);
                setDailyPrice(response.data.dailyPrice);
                setImagePath(response.data.imagePath);
                setOriginalTool(response.data);
            }).catch(error => {
                console.log("Something went wrong", error);
            })

        } else {
            setTitleToolForm("Añadir nueva herramienta")
            setStock(1);

        }
    }, [id]);

    if (!isAdmin && !id) {
        return null;
    }

    return (

        <Form onSubmit={saveTool}>
            <h3>{titleToolForm}</h3>

            <Form.Group className="mb-3" controlId="formToolName">
                <Form.Label>Nombre de la herramienta</Form.Label>
                <Form.Control type="text" placeholder="Ingrese el nombre de la herramienta" value={toolName}
                    onChange={(e) => setName(e.target.value)}
                    required
                />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formDescription">
                <Form.Label>Descripción</Form.Label>
                <Form.Control as="textarea" rows={3} placeholder="Ingrese la descripción"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                />

            </Form.Group>

            <Form.Group className="mb-3" controlId="formReplacementValue">
                <Form.Label>Valor de Reemplazo</Form.Label>
                <InputGroup>
                    <InputGroup.Text>$</InputGroup.Text>
                    <Form.Control
                        type="number"
                        value={replacementValue}
                        onChange={(e) => setReplacementValue(parseFloat(e.target.value) || 0)}
                        placeholder="0"
                        min="0"
                        step="1"
                        required
                        disabled={!isAdmin}
                    />
                </InputGroup>

            </Form.Group>

            <Form.Group className="mb-3" controlId="formCategory">
                <Form.Label>Categoría</Form.Label>
                <Form.Control
                    type="text"
                    placeholder="Ingrese la categoria"
                    value={category}
                    onChange={(e) => setCategory(e.target.value)}
                    required
                />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formStock">
                <Form.Label>Stock</Form.Label>
                <Form.Control
                    type="number"
                    value={stock}
                    onChange={(e) => setStock(parseInt(e.target.value) || 0)}
                    placeholder="0"
                    min="1"
                    step="1"
                    required
                />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formDailyPrice">
                <Form.Label>Precio Diario</Form.Label>
                <InputGroup>
                    <InputGroup.Text>$</InputGroup.Text>
                    <Form.Control
                        type="number"
                        value={dailyPrice}
                        onChange={(e) => setDailyPrice(parseFloat(e.target.value) || 0)}
                        placeholder="0"
                        min="0"
                        step="1"
                        required
                        disabled={!isAdmin}
                    />
                </InputGroup>
            </Form.Group>

            <Form.Group className="mb-3" controlId="formImage">
                <Form.Label>Imagen de la Herramienta</Form.Label>
                <Form.Control
                    type="file"
                    accept="image/*"
                    onChange={handleImageChange}
                />
                <Form.Text className="text-muted">
                    Selecciona una imagen para la herramienta (opcional)
                </Form.Text>
            </Form.Group>

            {(imagePreview || (imagePath && !imagePreview)) && (
                <Form.Group className="mb-3">
                    <Form.Label>
                        {imagePreview ? "Vista Previa" : "Imagen Actual"}
                    </Form.Label>
                    <div>
                        <img
                            src={imagePreview || (imagePath && imagePath.startsWith('/uploads') ? API_BASE + imagePath : imagePath)}
                            alt={imagePreview ? "Vista previa" : "Imagen actual de la herramienta"}
                            style={{
                                maxWidth: "200px",
                                maxHeight: "200px",
                                objectFit: "cover",
                                border: "1px solid #ddd",
                                borderRadius: "4px",
                                padding: "4px"
                            }}
                        />
                        {imagePreview && imagePath && (
                            <Form.Text className="text-muted d-block mt-1">
                                Se reemplazará la imagen actual
                            </Form.Text>
                        )}
                    </div>
                </Form.Group>
            )}

            <Button type="submit" variant="outline-dark">Guardar</Button>
            <Link to="/inventory" className="btn btn-secondary" style={{ marginLeft: "10px" }}>Cancelar</Link>

        </Form>

    );



};

export default ToolAdd;