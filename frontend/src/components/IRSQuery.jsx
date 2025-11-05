import { useState } from 'react';
import { Box, TextField, Button, Paper, Typography, CircularProgress, Chip } from '@mui/material';
import { Search, Home, Update, TrendingUp, CalendarMonth, AttachMoney } from '@mui/icons-material';
import axios from 'axios';

const API_BASE = 'http://localhost:8080';

export default function IRSQuery({ onBack }) {
  const [query, setQuery] = useState('');
  const [result, setResult] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    if (!query.trim()) return;
    
    setLoading(true);
    try {
      const response = await axios.post(`${API_BASE}/api/openai-search/query`, {
        question: query
      });
      setResult(response.data.answer);
    } catch (error) {
      console.error('Search failed:', error);
      setResult('Sorry, there was an error processing your query. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleQuickQuery = async (endpoint) => {
    setLoading(true);
    setQuery('');
    try {
      const response = await axios.get(`${API_BASE}/api/openai-search/${endpoint}`);
      setResult(response.data.answer);
    } catch (error) {
      console.error('Failed to fetch:', error);
      setResult('Sorry, there was an error. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ maxWidth: 1200, mx: 'auto', p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 700, color: '#ff9800', display: 'flex', alignItems: 'center', gap: 1 }}>
          <Search /> Query IRS 2025
        </Typography>
        <Button
          variant="outlined"
          startIcon={<Home />}
          onClick={onBack}
          sx={{ 
            borderColor: '#003d82', 
            color: '#003d82',
            '&:hover': { borderColor: '#002952', bgcolor: '#e3f2fd' }
          }}
        >
          Back to Home
        </Button>
      </Box>

      <Paper elevation={0} sx={{ p: 4, mb: 3, border: '2px solid #fff3e0', borderRadius: 3 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h6" sx={{ fontWeight: 600, color: '#424242' }}>
            Search IRS 2025 Information
          </Typography>
          <Chip label="Real-time Web Search" color="success" size="small" />
        </Box>
        
        <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
          <TextField
            fullWidth
            placeholder="e.g., What are the 2025 IRS standard deduction amounts?"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyPress={(e) => {
              if (e.key === 'Enter') handleSearch();
            }}
            disabled={loading}
            sx={{ bgcolor: 'white' }}
          />
          <Button
            variant="contained"
            startIcon={<Search />}
            onClick={handleSearch}
            disabled={loading || !query.trim()}
            sx={{ 
              minWidth: 120,
              bgcolor: '#ff9800',
              '&:hover': { bgcolor: '#f57c00' }
            }}
          >
            Search
          </Button>
        </Box>

        <Typography variant="subtitle2" sx={{ mb: 1, color: '#666' }}>Quick Queries:</Typography>
        <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
          <Button
            variant="outlined"
            size="small"
            startIcon={<Update />}
            onClick={() => handleQuickQuery('latest-updates')}
            disabled={loading}
            sx={{ borderColor: '#ff9800', color: '#ff9800' }}
          >
            Latest Updates
          </Button>
          <Button
            variant="outlined"
            size="small"
            startIcon={<TrendingUp />}
            onClick={() => handleQuickQuery('tax-brackets')}
            disabled={loading}
            sx={{ borderColor: '#ff9800', color: '#ff9800' }}
          >
            Tax Brackets
          </Button>
          <Button
            variant="outlined"
            size="small"
            startIcon={<AttachMoney />}
            onClick={() => handleQuickQuery('standard-deduction')}
            disabled={loading}
            sx={{ borderColor: '#ff9800', color: '#ff9800' }}
          >
            Standard Deduction
          </Button>
          <Button
            variant="outlined"
            size="small"
            startIcon={<CalendarMonth />}
            onClick={() => handleQuickQuery('filing-deadlines')}
            disabled={loading}
            sx={{ borderColor: '#ff9800', color: '#ff9800' }}
          >
            Filing Deadlines
          </Button>
        </Box>
      </Paper>

      {loading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', py: 8 }}>
          <CircularProgress sx={{ color: '#ff9800' }} />
          <Typography sx={{ ml: 2, color: '#5f6368' }}>Searching IRS.gov for 2025 information...</Typography>
        </Box>
      )}

      {result && !loading && (
        <Paper elevation={0} sx={{ p: 4, bgcolor: '#f8fafc', border: '1px solid #e2e8f0', borderRadius: 3 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
            <Typography variant="h6" sx={{ fontWeight: 600, color: '#424242' }}>
              IRS 2025 Information
            </Typography>
            <Chip label="From IRS.gov" color="primary" size="small" variant="outlined" />
          </Box>
          <Typography 
            variant="body1" 
            sx={{ 
              whiteSpace: 'pre-wrap', 
              lineHeight: 1.8,
              color: '#334155',
              '& strong': { color: '#ff9800', fontWeight: 600 }
            }}
            dangerouslySetInnerHTML={{ __html: result.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>') }}
          />
        </Paper>
      )}
    </Box>
  );
}
