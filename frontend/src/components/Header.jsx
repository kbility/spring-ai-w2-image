import { Box, Typography, Chip } from '@mui/material';
import { AccountBalance, AutoAwesome, VerifiedUser } from '@mui/icons-material';

export default function Header() {
  return (
    <Box sx={{ textAlign: 'center', mb: 6, pb: 4, borderBottom: '2px solid #e0e0e0' }}>
      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 2.5, mb: 2 }}>
        <Box sx={{ 
          bgcolor: '#003d82',
          color: 'white', 
          p: 2.5, 
          borderRadius: 3,
          display: 'flex',
          boxShadow: '0 4px 12px rgba(0,61,130,0.25)'
        }}>
          <AccountBalance sx={{ fontSize: 48 }} />
        </Box>
        <Typography 
          variant="h3" 
          sx={{ 
            color: '#003d82',
            fontWeight: 800,
            letterSpacing: '-0.02em',
            fontSize: { xs: '1.75rem', md: '2.5rem' }
          }}
        >
          Tax Advisor Pro
        </Typography>
      </Box>
      <Typography variant="body1" sx={{ mb: 3, fontSize: '1.1rem', color: '#5f6368', fontWeight: 500 }}>
        Professional Tax Document Analysis & Advisory Services
      </Typography>
      <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center', flexWrap: 'wrap' }}>
        <Chip 
          icon={<AutoAwesome />} 
          label="AI-Powered" 
          sx={{ 
            bgcolor: '#e8f5e9',
            color: '#00875a',
            fontWeight: 700,
            border: '2px solid #00875a',
            py: 2.5,
            fontSize: '0.9rem'
          }}
        />
        <Chip 
          icon={<VerifiedUser />} 
          label="Secure & Confidential" 
          sx={{ 
            bgcolor: '#e8f0fe',
            color: '#003d82',
            fontWeight: 700,
            border: '2px solid #003d82',
            py: 2.5,
            fontSize: '0.9rem'
          }}
        />
      </Box>
    </Box>
  );
}
