import { Dialog, DialogContent, IconButton, Box, Typography } from '@mui/material';
import { Close } from '@mui/icons-material';

export default function ImageModal({ image, onClose }) {
  return (
    <Dialog
      open={!!image}
      onClose={onClose}
      maxWidth="lg"
      fullWidth
      PaperProps={{
        sx: {
          bgcolor: 'rgba(0, 0, 0, 0.95)',
          boxShadow: 'none'
        }
      }}
    >
      <IconButton
        onClick={onClose}
        sx={{
          position: 'absolute',
          right: 8,
          top: 8,
          color: 'white',
          bgcolor: 'rgba(255, 255, 255, 0.1)',
          '&:hover': {
            bgcolor: 'rgba(255, 255, 255, 0.2)'
          }
        }}
      >
        <Close />
      </IconButton>
      <DialogContent sx={{ p: 0, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        {image && (
          <>
            <Box
              component="img"
              src={image}
              alt="Magnified W-2"
              sx={{
                maxWidth: '100%',
                maxHeight: '90vh',
                objectFit: 'contain'
              }}
            />
            <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.6)', mt: 2, mb: 2 }}>
              Click X or press ESC to close
            </Typography>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}
