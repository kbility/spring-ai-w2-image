import { Paper, Box, Typography, Table, TableBody, TableRow, TableCell, Chip } from '@mui/material';
import { Person } from '@mui/icons-material';

export default function ResultCard({ row, preview, index, setModalImage }) {
  const employeeName = row['Employee Name'] || row['employee_name'];
  const title = employeeName || `Document #${index + 1}`;

  return (
    <Paper 
      elevation={0}
      sx={{ 
        overflow: 'hidden',
        border: '1px solid #e3e8ef',
        transition: 'all 0.3s',
        '&:hover': {
          boxShadow: '0 8px 24px rgba(0,0,0,0.1)',
          borderColor: 'primary.main'
        }
      }}
    >
      <Box sx={{ 
        bgcolor: 'primary.main',
        color: 'white',
        p: 2,
        display: 'flex',
        alignItems: 'center',
        gap: 1
      }}>
        <Person />
        <Typography variant="h6" sx={{ fontWeight: 600, fontSize: '1rem' }}>
          {title}
        </Typography>
      </Box>

      <Box 
        sx={{ 
          maxHeight: 400, 
          overflow: 'auto', 
          bgcolor: '#fafbfc',
          borderBottom: '1px solid #e3e8ef',
          display: 'flex',
          justifyContent: 'center',
          p: 2,
          cursor: 'zoom-in',
          position: 'relative',
          '&:hover::after': {
            content: '"Click to enlarge"',
            position: 'absolute',
            bottom: 8,
            right: 8,
            bgcolor: 'rgba(0,0,0,0.7)',
            color: 'white',
            px: 1.5,
            py: 0.5,
            borderRadius: 1,
            fontSize: '0.75rem'
          }
        }}
        onClick={() => setModalImage(preview)}
      >
        <img 
          src={preview} 
          alt="W-2 Preview" 
          style={{ 
            maxWidth: '100%', 
            height: 'auto',
            transition: 'transform 0.3s',
            borderRadius: '4px'
          }}
        />
      </Box>

      <Box sx={{ p: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
          <Typography variant="subtitle1" sx={{ fontWeight: 600, color: '#1a1a1a' }}>
            Extracted Information
          </Typography>
          <Chip label={`${Object.keys(row).length} fields`} size="small" color="primary" variant="outlined" />
        </Box>
        <Table size="small">
          <TableBody>
            {Object.entries(row).map(([key, value]) => (
              <TableRow 
                key={key}
                sx={{
                  '&:last-child td': { border: 0 },
                  '&:hover': { bgcolor: '#f8fafc' }
                }}
              >
                <TableCell sx={{ fontWeight: 600, color: '#475569', width: '40%', py: 1.5 }}>
                  {key}
                </TableCell>
                <TableCell sx={{ color: '#1a1a1a', py: 1.5 }}>{String(value)}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Box>
    </Paper>
  );
}
