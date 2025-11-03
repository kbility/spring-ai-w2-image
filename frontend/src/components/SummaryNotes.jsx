import { useState, useEffect } from 'react';
import { Box, Typography, Button, Paper, Snackbar, Alert } from '@mui/material';
import { ContentCopy, Description } from '@mui/icons-material';
import axios from 'axios';

const API_BASE = 'http://localhost:8080';

export default function SummaryNotes({ employeeName, shouldRefresh, onRefreshComplete }) {
  const [summary, setSummary] = useState('');
  const [loading, setLoading] = useState(false);
  const [copied, setCopied] = useState(false);

  useEffect(() => {
    console.log('SummaryNotes useEffect:', { employeeName, shouldRefresh });
    if (employeeName && shouldRefresh) {
      fetchSummary();
    }
  }, [employeeName, shouldRefresh]);

  const fetchSummary = async () => {
    console.log('fetchSummary called for:', employeeName);
    setLoading(true);
    try {
      const response = await axios.get(`${API_BASE}/summary/${encodeURIComponent(employeeName)}`);
      console.log('Summary response:', response.data);
      setSummary(response.data.summary);
      if (onRefreshComplete) {
        onRefreshComplete();
      }
    } catch (error) {
      console.error('Failed to fetch summary:', error);
      setSummary('Failed to generate summary. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(summary);
      setCopied(true);
    } catch (error) {
      console.error('Failed to copy:', error);
    }
  };

  if (!employeeName) return null;

  return (
    <Paper 
      elevation={0}
      sx={{ 
        mt: 3, 
        p: 3, 
        bgcolor: '#f8fafc',
        border: '2px solid #e2e8f0'
      }}
    >
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h6" sx={{ fontWeight: 600, display: 'flex', alignItems: 'center', gap: 1 }}>
          <Description fontSize="small" />
          Summary Notes
        </Typography>
        <Button
          variant="outlined"
          size="small"
          startIcon={<ContentCopy />}
          onClick={handleCopy}
          disabled={!summary || loading}
          sx={{ textTransform: 'none' }}
        >
          Copy to Clipboard
        </Button>
      </Box>
      
      <Box 
        sx={{ 
          bgcolor: 'white',
          p: 2,
          borderRadius: 1,
          border: '1px solid #e2e8f0',
          fontFamily: 'monospace',
          fontSize: '0.875rem',
          whiteSpace: 'pre-wrap',
          maxHeight: 400,
          overflow: 'auto'
        }}
      >
        {loading ? 'Generating summary...' : summary || 'Answer "No, generate summary" in the chat section below to create a summary of the conversation.'}
      </Box>

      <Snackbar
        open={copied}
        autoHideDuration={2000}
        onClose={() => setCopied(false)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert severity="success" onClose={() => setCopied(false)}>
          Summary copied to clipboard!
        </Alert>
      </Snackbar>
    </Paper>
  );
}