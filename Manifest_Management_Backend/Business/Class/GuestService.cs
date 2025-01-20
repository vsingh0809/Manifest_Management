using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ManifestPortal1.DataAccess.Repository.Classes;
using ManifestPortal1.Business.Contracts;
using ManifestPortal1.DataAccess.Repository.Contracts;
using ManifestPortal1.Entities.DTO;
using ManifestPortal1.Entities.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MySqlX.XDevAPI.Common;
using Microsoft.AspNetCore.Http.HttpResults;


namespace ManifestPortal1.Business.Class
{
    public class GuestService : IGuestService
    {
        private readonly IGuestRepository _guestRepository;

        public GuestService(IGuestRepository guestRepository)
        {
            _guestRepository = guestRepository;
        }

        public async Task<Guest> GetGuestAsync(int id)
        {
            var guest = await _guestRepository.GetGuestByIdAsync(id);
            if (guest == null)
            {
                throw new KeyNotFoundException("Guest not found.");
            }
            return guest;
        }

        public async Task<IEnumerable<Guest>> GetAllGuestsAsync()
        {
            return await _guestRepository.GetAllGuestsAsync();
        }


        public async Task<Guest> RegisterGuestAsync(AddGuestDTO guestDto)
        {
            // Map DTO to Entity
            var existingGuest = await _guestRepository.GetGuestByEmailAsync(guestDto.Email);

            if (existingGuest == null || existingGuest.IsDeleted == "Y")
            {

                var guestEntity = new Guest
                {
                    FirstName = guestDto.FirstName,
                    LastName = guestDto.LastName,
                    //Gender = Enum.Parse<Gender>(guestDto.Gender), // Assuming GenderDTO is an enum
                    Gender = guestDto.Gender,
                    //Salutation = Enum.Parse<Salutation>(guestDto.Salutation),
                    Salutation = guestDto.Salutation,
                    MobileNo = guestDto.MobileNo,
                    Email = guestDto.Email,
                    BoardingPoint = guestDto.BoardingPoint,
                    DestinationPoint = guestDto.DestinationPoint,
                    LastUpdatedDate = DateTime.Now,
                    CreatedBy = string.IsNullOrEmpty(guestDto.createdBy) ? "Agent" : guestDto.createdBy,
                    LastUpdatedBy = string.IsNullOrEmpty(guestDto.LastUpdatedBy) ? "Agent" : guestDto.LastUpdatedBy,
                    Citizenship = string.IsNullOrEmpty(guestDto.Citizenship) ? "USA" : guestDto.Citizenship, // Default to "India" if empty
                    Dob = guestDto.DOB == default ? DateTime.Now : guestDto.DOB, // Default to current date if not provided
                    CreatedDate = DateTime.Now,

                };


                var createdGuest = await _guestRepository.AddGuestAsync(guestEntity);


                return createdGuest;
            }
            else if(existingGuest.IsDeleted == "N")
            {
                throw new InvalidOperationException("Guest with the provided email is already registered.");
            }
            return null;
        }




        public async Task RegisterGuestAsync(Guest guest)
        {
            // Save the guest entity to the database
            await _guestRepository.AddGuestAsync(guest);
        }

        // soft delete service

        public async Task SoftDeleteGuestAsync(int id)
        {
            var guest = await _guestRepository.GetGuestByIdAsync(id);
            if (guest == null)
            {
                throw new KeyNotFoundException("Guest not found.");

            }
            else if (guest.IsDeleted == "Y")
            {
                throw new KeyNotFoundException($"{nameof(guest)} with guest ID :{guest.GuestId} and name : {guest.FirstName} is  deleted already");
            }
            guest.IsDeleted = "Y";
            await _guestRepository.UpdateGuestForSoftDeleteAsync(guest);
        }

        //Soft delete multiple guest
        public async Task SoftMultipleDeleteGuestAsync(List<int> ids)
        {
            foreach(var guestId in ids)
            {
                var guest = await _guestRepository.GetGuestByIdAsync(guestId);
                if(guest == null)
                {
                    throw new KeyNotFoundException("Guest not found");
                }
                else if (guest.IsDeleted == "Y")
                {
                    throw new KeyNotFoundException($"{nameof(guest)} with guest ID :{guest.GuestId} and name : {guest.FirstName} is  deleted already");
                }
                guest.IsDeleted= "Y";
                await _guestRepository.UpdateGuestAsync(guest);

            }
        }

        public async Task UpdateGuestAsync(int id, UpdateGuestDTO updatedGuest)
        {
            var guest = await _guestRepository.GetGuestByIdAsync(id);
            if (guest == null)
            {
                throw new KeyNotFoundException("Guest not found.");
            }

            guest.FirstName = updatedGuest.FirstName;
            guest.LastName = updatedGuest.LastName;
            //guest.Gender = (Gender?)updatedGuest.Gender;
            //guest.Gender = Enum.Parse<Gender>(updatedGuest.Gender);
            guest.Gender = updatedGuest.Gender;
            guest.MobileNo = updatedGuest.MobileNo;
            guest.Citizenship = updatedGuest.Citizenship;
            guest.BoardingPoint = updatedGuest.BoardingPoint;
            guest.DestinationPoint = updatedGuest.DestinationPoint;




            await _guestRepository.UpdateGuestAsync(guest);
        }

        public async Task DeleteGuestAsync(int id)
        {
            await _guestRepository.DeleteGuestAsync(id);
        }


        public async Task<List<Guest>> SearchGuestsAsync(DateTime? embarkDate, DateTime? dbarkDate)
        {
            return await _guestRepository.SearchGuestsAsync(embarkDate, dbarkDate);
        }

        public async Task<List<Guest>> SearchByEmbarkDateAsync(DateTime embarkDate)
        {
            return await _guestRepository.SearchByEmbarkDateAsync(embarkDate);
        }

        public async Task<List<Guest>> SearchByDebarkDateAsync(DateTime debarkDate)
        {
            return await _guestRepository.SearchByDebarkDateAsync(debarkDate);
        }
        public Task<List<Guest>> SerachByEmbarkDate(string embarkDate)
        {
            return _guestRepository.SearchByEmbarkDate(embarkDate);
        }

        public Task<List<Guest>> SerachByDebarkDate(string deBarkDate)
        {
            return _guestRepository.SearchByDebarkDate(deBarkDate);
        }



        public async Task DeleteGuestsAsync(List<int> ids)
        {
            foreach (var id in ids)
            {
                var guest = await _guestRepository.GetGuestByIdAsync(id);
                if (guest == null)
                {
                    throw new KeyNotFoundException($"Guest with ID {id} not found.");
                }


                await _guestRepository.DeleteGuestAsync(id);
            }


        }

        public async Task<List<Guest>> GetGuestsBySalutationAsync(string salutation)
        {
            return await _guestRepository.GetGuestsByTitleAsync(salutation);
        }


        public async Task<ActionResult> CheckInGuestAsync(int barcode)
        {
            //Validate barcode and fetch the guest from the repository
            var guest = await _guestRepository.GetGuestByIdAsync(barcode);
            if (guest == null)
            {
                return new NotFoundObjectResult(new { message = $"Guest with ID {barcode} not found." });
            }
            if (guest.IsDeleted == "Y")
            {
                return new BadRequestObjectResult(new { message = $"Guest with Id: {barcode} was deleted" });
            }
            //Check if the guest has already checked in (MBarkDate is not null)
            if (guest.MBarkDate.HasValue)
            {
                return new BadRequestObjectResult(new { message = $"Guest with Id: {guest.GuestId} and name: {guest.FirstName} is already Checked-In" });
            }

            //Update the MBarkDate (check-in date) to the current date and time
            guest.MBarkDate = DateTime.UtcNow;

            await _guestRepository.UpdateGuestAsync(guest);

            return new OkObjectResult(new { message = "Check-in successful." });
        }

        public async Task<ActionResult> CheckOutGuestAsync(int barcode)
        {
            //Validate barcode and fetch the guest from the repository
            var guest = await _guestRepository.GetGuestByIdAsync(barcode);
            if (guest == null)
            {
                return new NotFoundObjectResult(new { message = $"Guest with ID {barcode} not found." });
            }
            if (guest.IsDeleted == "Y")
            {
                return new BadRequestObjectResult(new { message = $"Guest with Id {guest.GuestId} and name: {guest.FirstName} was deleted" });
            }

            //Check if the guest has already checked in (MBarkDate is not null)
            if (!guest.MBarkDate.HasValue)
            {
                return new BadRequestObjectResult(new { message = "Guest must be checked in to check out." });
            }

            //Update the MBarkDate (check-in date) to the current date and time
            guest.DBarkDate = DateTime.UtcNow;

            await _guestRepository.UpdateGuestAsync(guest);

            return new OkObjectResult(new { message = "Check-out successful." });
        }


        public async Task<ServiceResponseDTO> UpdateGuestImageAsync(int crewId, UpdateGuestImageRequestDTO request)
        {
            try
            {
                var guestOperator = await _guestRepository.GetGuestOperatorByIdAsync(crewId);

                if (guestOperator == null)
                {
                    return new ServiceResponseDTO { Success = false, StatusCode = 404, Message = "Crew operator not found." };
                }

                // Decode Base64 string to binary
                var imageBytes = Convert.FromBase64String(request.ImageBase64);

                // Update the CrewImage field
                guestOperator.GuestImage = imageBytes;

                // Save changes
                await _guestRepository.UpdateGuestOperatorAsync(guestOperator);

                return new ServiceResponseDTO { Success = true, Message = "Crew image updated successfully." };
            }
            catch (FormatException)
            {
                return new ServiceResponseDTO { Success = false, StatusCode = 400, Message = "Invalid Base64 string." };
            }
            catch (Exception ex)
            {
                return new ServiceResponseDTO { Success = false, StatusCode = 500, Message = $"Internal server error: {ex.Message}" };
            }
        }

    }

}

