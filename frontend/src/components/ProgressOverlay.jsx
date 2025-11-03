import { Backdrop, Box, CircularProgress, Typography, LinearProgress } from '@mui/material';
import { Psychology } from '@mui/icons-material';

export default function ProgressOverlay({ open }) {
  return (
    <Backdrop
      sx={{ 
        color: '#fff', 
        zIndex: (theme) => theme.zIndex.drawer + 1,
        bgcolor: 'rgba(0, 0, 0, 0.85)',
        backdropFilter: 'blur(4px)'
      }}
      open={open}
    >
      <Box sx={{ textAlign: 'center', maxWidth: 400 }}>
        <Box sx={{ position: 'relative', display: 'inline-flex', mb: 3 }}>
          <CircularProgress 
            size={80} 
            thickness={3}
            sx={{ color: '#1976d2' }} 
          />
          <Box sx={{
            position: 'absolute',
            top: 0,
            left: 0,
            bottom: 0,
            right: 0,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}>
            <Psychology sx={{ fontSize: 40, color: '#1976d2' }} />
          </Box>
        </Box>
        
        <Typography variant="h5" sx={{ mb: 1, fontWeight: 600 }}>
          Processing W-2 Forms
        </Typography>
        <Typography variant="body2" sx={{ mb: 3, color: 'rgba(255,255,255,0.7)' }}>
          AI is analyzing your documents...
        </Typography>
        
        <Box sx={{ width: '100%' }}>
          <LinearProgress 
            sx={{
              height: 6,
              borderRadius: 3,
              bgcolor: 'rgba(255, 255, 255, 0.1)',
              '& .MuiLinearProgress-bar': {
                bgcolor: '#1976d2',
                borderRadius: 3
              }
            }}
          />
        </Box>
      </Box>
    </Backdrop>
  );
}
