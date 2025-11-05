import { useState } from 'react';
import { Box, Typography, Button, Grid, Divider, Paper } from '@mui/material';
import { CheckCircle, Download, FolderOpen } from '@mui/icons-material';
import ResultCard from './ResultCard';
import W2Chat from './W2Chat';
import SummaryNotes from './SummaryNotes';

export default function ResultsSection({ results, setModalImage }) {
  const [shouldRefreshSummary, setShouldRefreshSummary] = useState(false);

  const handleSummaryGenerated = () => {
    console.log('handleSummaryGenerated called');
    setShouldRefreshSummary(true);
  };

  const handleSummaryRefreshComplete = () => {
    console.log('handleSummaryRefreshComplete called');
    setShouldRefreshSummary(false);
  };
  if (!results || !results.table || results.table.length === 0) {
    return (
      <Box sx={{ 
        textAlign: 'center', 
        py: 10, 
        bgcolor: '#fafbfc',
        borderRadius: 2,
        border: '2px dashed #e3e8ef'
      }}>
        <FolderOpen sx={{ fontSize: 80, mb: 2, color: '#cbd5e1' }} />
        <Typography variant="h5" sx={{ mb: 1, color: '#475569', fontWeight: 600 }}>
          No Data Yet
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Upload W-2 forms above to extract and view tax information
        </Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ mt: 5 }}>
      <Divider sx={{ mb: 4 }} />
      
      <Box sx={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center', 
        mb: 4, 
        flexWrap: 'wrap', 
        gap: 2,
        bgcolor: '#f0f9ff',
        p: 2.5,
        borderRadius: 2,
        border: '1px solid #bae6fd'
      }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
          <CheckCircle sx={{ color: '#22c55e', fontSize: 28 }} />
          <Box>
            <Typography variant="h5" sx={{ fontWeight: 600, color: '#1a1a1a' }}>
              Extraction Complete
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {results.table.length} document{results.table.length > 1 ? 's' : ''} processed successfully
            </Typography>
          </Box>
        </Box>
        <Button
          variant="contained"
          startIcon={<Download />}
          href="http://localhost:8080/download"
          sx={{ 
            py: 1.5,
            px: 3,
            fontWeight: 600,
            textTransform: 'none',
            fontSize: '1rem'
          }}
        >
          Download Excel
        </Button>
      </Box>

      <Grid container spacing={3}>
        {results.table.map((row, index) => (
          <Grid item xs={12} lg={6} key={index}>
            <ResultCard 
              row={row} 
              preview={results.previews[index]} 
              index={index}
              setModalImage={setModalImage}
            />
          </Grid>
        ))}
      </Grid>

      <SummaryNotes 
        employeeName={results.recipientName || results.table[0]?.['Employee Name'] || results.table[0]?.['employee_name']} 
        shouldRefresh={shouldRefreshSummary}
        onRefreshComplete={handleSummaryRefreshComplete}
      />

      <Paper 
        elevation={0}
        sx={{ 
          mt: 4, 
          p: 3, 
          bgcolor: '#fafbfc',
          border: '2px solid #e3e8ef'
        }}
      >
        <Typography variant="h6" sx={{ mb: 2, fontWeight: 600 }}>
          Ask Questions About Employee
        </Typography>
        <W2Chat 
          employeeName={results.recipientName || results.table[0]?.['Employee Name'] || results.table[0]?.['employee_name']} 
          onSummaryGenerated={handleSummaryGenerated}
        />
      </Paper>
    </Box>
  );
}
