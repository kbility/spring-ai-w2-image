import { useState } from 'react';
import { Box, Paper, Typography, Button } from '@mui/material';
import { CloudUpload, RocketLaunch } from '@mui/icons-material';
import axios from 'axios';

const API_BASE = 'http://localhost:8080';

export default function UploadForms({ setResults, setLoading }) {
  const [singleFile, setSingleFile] = useState(null);
  const [multiFiles, setMultiFiles] = useState(null);

  const handleSingleUpload = async (e) => {
    e.preventDefault();
    if (!singleFile) return;

    setLoading(true);
    const formData = new FormData();
    formData.append('file', singleFile);

    try {
      const response = await axios.post(`${API_BASE}/upload`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      sessionStorage.setItem('w2Results', JSON.stringify(response.data));
      window.location.reload();
    } catch (error) {
      console.error('Upload failed:', error);
      alert('Upload failed. Please try again.');
      setLoading(false);
    }
  };

  const handleMultiUpload = async (e) => {
    e.preventDefault();
    if (!multiFiles) return;

    setLoading(true);
    const formData = new FormData();
    Array.from(multiFiles).forEach(file => formData.append('files', file));

    try {
      const response = await axios.post(`${API_BASE}/upload-multi`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      sessionStorage.setItem('w2Results', JSON.stringify(response.data));
      window.location.reload();
    } catch (error) {
      console.error('Upload failed:', error);
      alert('Upload failed. Please try again.');
      setLoading(false);
    }
  };

  return (
    <Box sx={{ display: 'flex', gap: 4, flexWrap: 'wrap', mb: 5 }}>
      <Paper 
        component="form" 
        onSubmit={handleSingleUpload}
        elevation={0}
        sx={{ 
          flex: 1, 
          minWidth: 300, 
          p: 5,
          bgcolor: 'white',
          border: '3px solid #003d82',
          borderRadius: 3,
          transition: 'all 0.3s',
          '&:hover': {
            borderColor: '#002952',
            boxShadow: '0 8px 24px rgba(0,61,130,0.2)',
            transform: 'translateY(-4px)'
          }
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 4 }}>
          <Box sx={{ bgcolor: '#e3f2fd', p: 1.5, borderRadius: 2, display: 'flex' }}>
            <CloudUpload sx={{ color: '#003d82', fontSize: 32 }} />
          </Box>
          <Typography variant="h5" sx={{ color: '#003d82', fontWeight: 700 }}>
            Single Document
          </Typography>
        </Box>
        <Button
          variant="outlined"
          component="label"
          fullWidth
          sx={{ 
            mb: 3, 
            borderStyle: 'dashed', 
            borderWidth: 3,
            borderColor: '#003d82',
            py: 4,
            bgcolor: '#f8f9fa',
            color: '#003d82',
            fontWeight: 700,
            fontSize: '1rem',
            '&:hover': {
              bgcolor: '#e8f0fe',
              borderColor: '#002952',
              borderWidth: 3
            }
          }}
        >
          Choose File
          <input
            type="file"
            hidden
            accept="application/pdf,image/*"
            onChange={(e) => setSingleFile(e.target.files[0])}
          />
        </Button>
        {singleFile && (
          <Box sx={{ mb: 3, p: 2, bgcolor: '#e8f0fe', borderRadius: 2, border: '1px solid #003d82' }}>
            <Typography variant="body2" sx={{ color: '#003d82', fontWeight: 600 }}>
              ✓ {singleFile.name}
            </Typography>
          </Box>
        )}
        <Button
          type="submit"
          variant="contained"
          fullWidth
          disabled={!singleFile}
          startIcon={<RocketLaunch />}
          sx={{ 
            py: 2.5,
            fontWeight: 700,
            fontSize: '1.1rem',
            bgcolor: '#003d82',
            borderRadius: 2,
            boxShadow: '0 4px 12px rgba(0,61,130,0.3)',
            '&:hover': {
              bgcolor: '#002952',
              boxShadow: '0 6px 16px rgba(0,61,130,0.4)',
              transform: 'translateY(-2px)'
            },
            '&:disabled': {
              bgcolor: '#cbd5e1',
              color: '#94a3b8'
            },
            transition: 'all 0.3s ease'
          }}
        >
          Upload & Extract
        </Button>
      </Paper>

      <Paper 
        component="form" 
        onSubmit={handleMultiUpload}
        elevation={0}
        sx={{ 
          flex: 1, 
          minWidth: 300, 
          p: 5,
          bgcolor: 'white',
          border: '3px solid #00875a',
          borderRadius: 3,
          transition: 'all 0.3s',
          '&:hover': {
            borderColor: '#006644',
            boxShadow: '0 8px 24px rgba(0,135,90,0.2)',
            transform: 'translateY(-4px)'
          }
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 4 }}>
          <Box sx={{ bgcolor: '#e8f5e9', p: 1.5, borderRadius: 2, display: 'flex' }}>
            <CloudUpload sx={{ color: '#00875a', fontSize: 32 }} />
          </Box>
          <Typography variant="h5" sx={{ color: '#00875a', fontWeight: 700 }}>
            Multiple Documents
          </Typography>
        </Box>
        <Button
          variant="outlined"
          component="label"
          fullWidth
          sx={{ 
            mb: 3, 
            borderStyle: 'dashed', 
            borderWidth: 3,
            borderColor: '#00875a',
            py: 4,
            bgcolor: '#f8f9fa',
            color: '#00875a',
            fontWeight: 700,
            fontSize: '1rem',
            '&:hover': {
              bgcolor: '#e8f5e9',
              borderColor: '#006644',
              borderWidth: 3
            }
          }}
        >
          Choose Files
          <input
            type="file"
            hidden
            multiple
            accept="application/pdf,image/*"
            onChange={(e) => setMultiFiles(e.target.files)}
          />
        </Button>
        {multiFiles && (
          <Box sx={{ mb: 3, p: 2, bgcolor: '#e8f5e9', borderRadius: 2, border: '1px solid #00875a' }}>
            <Typography variant="body2" sx={{ color: '#00875a', fontWeight: 600 }}>
              ✓ {multiFiles.length} file(s) selected
            </Typography>
          </Box>
        )}
        <Button
          type="submit"
          variant="contained"
          fullWidth
          disabled={!multiFiles}
          startIcon={<RocketLaunch />}
          sx={{ 
            py: 2.5,
            fontWeight: 700,
            fontSize: '1.1rem',
            bgcolor: '#00875a',
            borderRadius: 2,
            boxShadow: '0 4px 12px rgba(0,135,90,0.3)',
            '&:hover': {
              bgcolor: '#006644',
              boxShadow: '0 6px 16px rgba(0,135,90,0.4)',
              transform: 'translateY(-2px)'
            },
            '&:disabled': {
              bgcolor: '#cbd5e1',
              color: '#94a3b8'
            },
            transition: 'all 0.3s ease'
          }}
        >
          Upload & Extract All
        </Button>
      </Paper>
    </Box>
  );
}
