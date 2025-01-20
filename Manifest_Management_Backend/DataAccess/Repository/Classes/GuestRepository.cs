using ManifestPortal1.DataAccess.Repository.Contracts;
using ManifestPortal1.Entities.EFCore;
using ManifestPortal1.Entities.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Data.SqlClient;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace ManifestPortal1.DataAccess.Repository.Classes
{
    public class GuestRepository : IGuestRepository
    {
        private readonly TeamAContext _context;



        public GuestRepository(TeamAContext context)
        {
            _context = context;
        }

        public async Task<Guest> GetGuestByIdAsync(int id)
        {
            
           
                if (id <= 0)
                    throw new ArgumentException("ID must be a positive integer.", nameof(id));

                var guest = await _context.Guests.FindAsync(id);
                if (guest == null)
                {
                    throw new KeyNotFoundException($"Guest with ID {id} not found.");
                }
                if (guest.IsDeleted == "Y")
                {
                    throw new KeyNotFoundException($"Guest with ID: {id} and Name: {guest.FirstName} is deleted.");
                }

                return guest;
            
        }

        public async Task<Guest> GetGuestByEmailAsync(string email)
        { 
            return await _context.Guests.FirstOrDefaultAsync(g => g.Email == email && g.IsDeleted == "N");

        }


            
        public async Task<IEnumerable<Guest>> GetAllGuestsAsync()
        {
            try
            {
                var guests = await _context.Guests.Where(g => g.IsDeleted == "N").ToListAsync();



                return guests;
            }
            catch (Exception ex)
            {
                throw new Exception("An error occurred while fetching all guests.", ex);
            }
        }

        public async Task<Guest> AddGuestAsync(Guest guest)
        {
            try
            {
                await _context.Guests.AddAsync(guest);
                await _context.SaveChangesAsync();
                return guest;
            }
            catch (Exception ex)
            {
                throw new Exception("An error occurred while adding a new guest.", ex);
            }
        }


        // -------------------------------Soft delete Guest's------------------------------------------------//

        //Soft delete single guest by id also use this update guest fxn



        public async Task UpdateGuestAsync(Guest guest)
        {
            try
            {
                if (!_context.Guests.Any(g => g.GuestId == guest.GuestId))
                    throw new KeyNotFoundException($"Guest with ID {guest.GuestId} not found.");

                _context.Guests.Update(guest);
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while updating the guest with ID {guest.GuestId}.", ex);
            }
        }


        public async Task UpdateGuestForSoftDeleteAsync(Guest guest)
        {
            try
            {
                if (!_context.Guests.Any(g => g.GuestId == guest.GuestId))
                {
                    throw new KeyNotFoundException($"Guest with ID {guest.GuestId} not found.");
                }
                int id = guest.GuestId;
                // Execute the stored procedure
                
                await _context.Database.ExecuteSqlRawAsync("CALL MoveToBackupTableOnDelete({0});", id);

                _context.Guests.Update(guest);
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while updating the guest with ID {guest.GuestId}.", ex);
            }
        }


        public async Task DeleteGuestAsync(int id)
        {
            try
            {
                var guest = await _context.Guests.FindAsync(id);
                if (guest == null)
                    throw new KeyNotFoundException($"Guest with ID {id} not found.");

                _context.Guests.Remove(guest);
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while deleting the guest with ID {id}.", ex);
            }
        }

        public async Task DeleteMultipleGuestsAsync(List<int> ids)
        {
            try
            {
                var guests = await _context.Guests.Where(g => ids.Contains(g.GuestId)).ToListAsync();
                if (guests.Count != ids.Count)
                    throw new KeyNotFoundException("One or more guests not found.");

                _context.Guests.RemoveRange(guests);
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                throw new Exception("An error occurred while deleting multiple guests.", ex);
            }
        }

        public async Task<List<Guest>> SearchGuestsAsync(DateTime? embarkDate, DateTime? dbarkDate)
        {
            try
            {
                var query = _context.Guests.AsQueryable();

                if (embarkDate.HasValue)
                {
                    query = query.Where(g => g.MBarkDate.HasValue && g.MBarkDate.Value.Date >= embarkDate.Value.Date && g.IsDeleted == "N");
                }

                if (dbarkDate.HasValue)
                {
                    query = query.Where(g => g.DBarkDate.HasValue && g.DBarkDate.Value.Date <= dbarkDate.Value.Date && g.IsDeleted =="N");
                }

                return await query.ToListAsync();
            }
            catch (Exception ex)
            {
                throw new Exception("An error occurred while searching for guests.", ex);
            }
        }

        public Task DeleteMultipleGuestAsync(int id)
        {
            throw new NotImplementedException();
        }


        public async Task<List<Guest>> GetGuestsByTitleAsync(string salutation)
        {
            try
            {
                return await _context.Guests
                                .Where(g => g.Salutation == salutation && g.IsDeleted == "N")
                                .ToListAsync();
            }
            catch (Exception ex)
            {
                throw new Exception("Error occurred while fetching the guest.", ex);
            }
        }

        //search by embark date


        public async Task<List<Guest>> SearchByEmbarkDateAsync(DateTime embarkDate)
        {
            try
            {
                return await _context.Guests.Where(g => g.MBarkDate.HasValue && g.MBarkDate.Value.Date == embarkDate.Date && g.IsDeleted == "N").ToListAsync();
            }
            catch (Exception ex)
            {
                throw new Exception("An error occured while searching kby embark date.", ex);


            }
        }



        public async Task<List<Guest>> SearchByDebarkDateAsync(DateTime dbarkDate)
        {
            try
            {
                return await _context.Guests.Where(g => g.DBarkDate.HasValue && g.DBarkDate.Value.Date == dbarkDate.Date && g.IsDeleted == "N").ToListAsync();
            }
            catch (Exception ex)
            {
                throw new Exception("An error occured while searching by dbark date = ", ex);
            }
        }

        public Task<List<Guest>> SearchByDebarkDate(string dBearkDate)
        {
            try
            {
                // Parse the input string to DateTime
                if (!DateTime.TryParse(dBearkDate, out var debarkDate))
                {
                    throw new ArgumentException("Invalid date format provided.");
                }

                // Filter guests by debark date
                return Task.FromResult(_context.Guests
                    .Where(g => g.DBarkDate.HasValue && g.DBarkDate.Value.Date == debarkDate.Date && g.IsDeleted == "N")
                    .ToList());
            }
            catch (Exception ex)
            {
                throw new Exception("An error occurred while searching by debark date.", ex);
            }
        }


        public Task<List<Guest>> SearchByEmbarkDate(string embarkDate)
        {
            try
            {
                // Parse the input string to DateTime
                if (!DateTime.TryParse(embarkDate, out var embarkDateTime))
                {
                    throw new ArgumentException("Invalid date format provided.");
                }

                // Filter guests by embark date
                return Task.FromResult(_context.Guests
                    .Where(g => g.MBarkDate.HasValue && g.MBarkDate.Value.Date == embarkDateTime.Date && g.IsDeleted == "N")
                    .ToList());
            }
            catch (Exception ex)
            {
                throw new Exception("An error occurred while searching by embark date.", ex);
            }
        }



        public async Task<Guest> GetGuestByBarcodeAsync(int barcode)
        {
            return await _context.Guests.FirstOrDefaultAsync(g => g.GuestId == barcode && g.IsDeleted =="N");
        }

        public async Task<Guest?> GetGuestOperatorByIdAsync(int guestId)
        {
            return await _context.Guests.FindAsync(guestId);
        }

        public async Task UpdateGuestOperatorAsync(Guest guestOperator)
        {
            _context.Guests.Update(guestOperator);
            await _context.SaveChangesAsync();
        }


        //public async Task UpdateGuestAsync(Guest guest)
        //{
        //    _context.Guests.Update(guest);
        //    await _context.SaveChangesAsync();
        //}
    }
}
