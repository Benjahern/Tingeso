import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import workerService from "../../services/worker.service";
import React from "react";
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import { PencilSquare, Trash, PersonPlusFill } from "react-bootstrap-icons";

const WorkerPage = () => {
  const [workers, setWorkers] = useState([]);
  const [displayedWorkers, setDisplayedWorkers] = useState([]);
  
  // Pagination state
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);

  const navigate = useNavigate();

  const init = (page = currentPage, size = pageSize) => {
    workerService.getAllWorkers().then(response => {
      const data = response.data;
      
      // If backend returns a Spring Page-like object
      if (data && data.content && Array.isArray(data.content)) {
        setWorkers(data.content);
        setDisplayedWorkers(data.content);
        setTotalPages(data.totalPages || 1);
        setCurrentPage((data.number || 0) + 1);
        return;
      }
      
      // If backend returns an array, compute client-side pages
      if (Array.isArray(data)) {
        setWorkers(data);
        const pages = Math.max(1, Math.ceil(data.length / size));
        setTotalPages(pages);
        setCurrentPage(Math.min(page, pages));
        
        const start = (Math.min(page, pages) - 1) * size;
        const paged = data.slice(start, start + size);
        setDisplayedWorkers(paged);
        return;
      }
      
      // Fallback
      const arr = data ? (Array.isArray(data) ? data : [data]) : [];
      setWorkers(arr);
      setDisplayedWorkers(arr);
      setTotalPages(1);
      setCurrentPage(1);
    }).catch(error => {
      console.log('Something went wrong', error);
    });
  };

  useEffect(() => {
    init();
  }, []);

  useEffect(() => {
    init(currentPage, pageSize);
  }, [currentPage]);

  const handleDelete = (id) => {
    const confirmDelete = window.confirm('¿Está seguro que desea eliminar este trabajador?');
    if (confirmDelete) {
      workerService.deleteWorker(id).then(response => {
        console.log('Worker deleted successfully', response.data);
        init();
      }).catch(error => {
        console.log('Something went wrong', error);
      });
    }
  };


  return (
    <div className="container mt-3">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>Trabajadores</h2>
        <Button 
          variant="success" 
          onClick={() => navigate('/employees/add')}
        >
          <PersonPlusFill className="me-2" />
          Agregar Trabajador
        </Button>
      </div>

      <Table striped bordered hover responsive>
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Email</th>
            <th>Rol</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {displayedWorkers.length > 0 ? (
            displayedWorkers.map(worker => (
              <tr key={worker.userId}>
                <td>{worker.name}</td>
                <td>{worker.mail}</td>
                <td>
                  {worker.rol && worker.rol.length > 0
                    ? worker.rol.map(role => role.rolName).join(', ')
                    : 'N/A'}


                </td>
                <td>
                  
                  <Button 
                    variant="outline-danger" 
                    size="sm"
                    onClick={() => handleDelete(worker.id)}
                  >
                    <Trash />
                  </Button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="4" className="text-center">No hay trabajadores registrados</td>
            </tr>
          )}
        </tbody>
      </Table>

      {/* Pagination */}
      <div className="d-flex justify-content-between align-items-center mt-3">
        <div>
          <span>Página {currentPage} de {totalPages}</span>
        </div>
        <div>
          <Button 
            variant="secondary" 
            size="sm" 
            className="me-2"
            disabled={currentPage === 1}
            onClick={() => setCurrentPage(currentPage - 1)}
          >
            Anterior
          </Button>
          <Button 
            variant="secondary" 
            size="sm"
            disabled={currentPage === totalPages}
            onClick={() => setCurrentPage(currentPage + 1)}
          >
            Siguiente
          </Button>
        </div>
        <div>
          <select 
            className="form-select form-select-sm" 
            style={{ width: 'auto' }}
            value={pageSize}
            onChange={(e) => {
              setPageSize(Number(e.target.value));
              setCurrentPage(1);
            }}
          >
            <option value={5}>5 por página</option>
            <option value={10}>10 por página</option>
            <option value={20}>20 por página</option>
            <option value={50}>50 por página</option>
          </select>
        </div>
      </div>
    </div>
  );
};

export default WorkerPage;
